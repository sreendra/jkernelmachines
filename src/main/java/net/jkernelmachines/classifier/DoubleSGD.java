/*******************************************************************************
 * Copyright (c) 2016, David Picard.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package net.jkernelmachines.classifier;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import net.jkernelmachines.kernel.typed.DoubleLinear;
import net.jkernelmachines.type.TrainingSample;
import net.jkernelmachines.type.TrainingSampleStream;

/**
 * <p>
 * Linear SVM classifier using stochastic gradient descent algorithm
 * </p>
 * 
 * <p>
 * <b>Large-Scale Machine Learning with Stochastic Gradient Descent</b>
 * Léon Bottou
 * <i>Proceedings of the 19th International Conference on Computational Statistics (COMPSTAT'2010)</i>
 * </p>
 * 
 * @author picard
 *
 */
public class DoubleSGD implements Classifier<double[]>, OnlineClassifier<double[]>, Serializable {

	private static final long serialVersionUID = 3245177176254451010L;
	
	// Available losses
	/** Type of loss function using hinge */
	public static final int HINGELOSS = 1;
	/** Type of loss function using a smoothed hinge */
	public static final int SMOOTHHINGELOSS = 2;
	/** Type of loss function using a squared hinge */
	public static final int SQUAREDHINGELOSS = 3;
	/** Type of loss function using log */
	public static final int LOGLOSS = 10;
	/** Type of loss function using margin log */
	public static final int LOGLOSSMARGIN = 11;
	
	//used loss function
	private int loss = HINGELOSS;

	//svm hyperplane
	private double[] w = null;
	double bias;
	boolean hasBias = true;
	//skipping decay update parameter
	private long t;
	private double lambda = 1e-4;
	private int epochs = 5;
	private double wscale;
	private boolean shuffle = false;
	
	//linear kernel
	DoubleLinear linear = new DoubleLinear();

	@Override
	public void train(List<TrainingSample<double[]>> l) {
		
		if(l.isEmpty())
			return;
		
		//new w
		w = new double[l.get(0).sample.length];
		
		//init
		wscale = 1; bias = 0;
		// Shift t in order to have a
		// reasonable initial learning rate.
		// This assumes |x| \approx 1.
		double maxw = 1.0 / Math.sqrt(lambda);
		double typw = Math.sqrt(maxw);
		double eta0 = typw / Math.max(1.0, dloss(-typw));
		t = (long) (1 / (eta0 * lambda));
			  
		for(int e = 0 ; e < epochs ; e++) {
			trainOnce(l);
		}

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.lip6.jkernelmachines.classifier.Classifier#train(fr.lip6.jkernelmachines
	 * .type.TrainingSample)
	 */
	@Override
	public void train(TrainingSample<double[]> sample) {
		if(w == null) {
			//new w
			w = new double[sample.sample.length];
			
			//init
			wscale = 1; bias = 0;
			// Shift t in order to have a
			// reasonable initial learning rate.
			// This assumes |x| \approx 1.
			double maxw = 1.0 / Math.sqrt(lambda);
			double typw = Math.sqrt(maxw);
			double eta0 = typw / Math.max(1.0, dloss(-typw));
			t = (long) (1 / (eta0 * lambda));
		}
		
		double eta = 1.0 / (lambda * t);
		double s = 1 - eta * lambda;
		wscale *= s;
		if (wscale < 1e-9) {
			for (int d = 0; d < w.length; d++)
				w[d] *= wscale;
			wscale = 1;
		}
		double[] x = sample.sample;
		double y = sample.label;
		double wx = linear.valueOf(w, x) * wscale;
		double z = y * (wx + bias);

		if (z < 1 && loss < LOGLOSS) {
			double etd = eta * dloss(z);
			for (int d = 0; d < w.length; d++)
				w[d] += x[d] * etd * y / wscale;
			// Slower rate on the bias because
			// it learns at each iteration.
			if(hasBias)
				bias += etd * y * 0.01;
		}
		t += 1;
	} 
	
	/* (non-Javadoc)
	 * @see fr.lip6.jkernelmachines.classifier.OnlineClassifier#onlineTrain(fr.lip6.jkernelmachines.type.TrainingSampleStream)
	 */
	@Override
	public void onlineTrain(TrainingSampleStream<double[]> stream) {
		TrainingSample<double[]> t;
		while((t = stream.nextSample()) != null) {
			train(t);
		}
	}
	
	/**
	 * Update the separating hyperplane by learning one epoch on given training list
	 * @param l the training list
	 */
	public void trainOnce(List<TrainingSample<double[]>> l) {
		if(w == null)
			return;
		
		int imax = l.size();
		if(shuffle) {
			Collections.shuffle(l);
		}
		
		for (int i = 0; i < imax; i++) {
			double eta = 1.0 / (lambda * t);
			double s = 1 - eta * lambda;
			wscale *= s;
			if (wscale < 1e-9) {
				for (int d = 0; d < w.length; d++)
					w[d] *= wscale;
				wscale = 1;
			}
			double[] x = l.get(i).sample;
			double y = l.get(i).label;
			double wx = linear.valueOf(w, x) * wscale;
			double z = y * (wx + bias);

			if (z < 1 && loss < LOGLOSS) {
				double etd = eta * dloss(z);
				for (int d = 0; d < w.length; d++)
					w[d] += x[d] * etd * y / wscale;
				// Slower rate on the bias because
				// it learns at each iteration.
				if(hasBias)
					bias += etd * y * 0.01;
			}
			t += 1;
		}

		if(!hasBias)
			bias = 0;
	}

	@Override
	public double valueOf(double[] e) {
		return linear.valueOf(w,e) * wscale + bias;
	}
	 
	private double dloss(double z)
	{
		switch(loss)
		{
		case LOGLOSS :
			if (z < 0)
				return 1 / (Math.exp(z) + 1);
			double ez = Math.exp(-z);
			return ez / (ez + 1);
		case LOGLOSSMARGIN :
			if (z < 1)
				return 1 / (Math.exp(z-1) + 1);
			ez = Math.exp(1-z);
			return ez / (ez + 1);
		case SMOOTHHINGELOSS :
			if (z < 0)
				return 1;
			if (z < 1)
				return 1-z;
			return 0;
		case SQUAREDHINGELOSS :
			if (z < 1)
				return (1 - z);
			return 0;
		default :
			if (z < 1)
				return 1;
			return 0;
		}
	}

	/**
	 * Tells the arrays of coordinate of separating hyperplane
	 * @return the arrays of coordinate of separating hyperplane
	 */
	public double[] getW() {
		return w;
	}

	/**
	 * Tells the type of loss used by this classifier (default HINGELOSS)
	 * @return an integer representing the type of loss
	 */
	public int getLoss() {
		return loss;
	}

	/**
	 * Sets the type of loss used by this classifier (default HINGELOSS)
	 * @param loss loss type
	 */
	public void setLoss(int loss) {
		this.loss = loss;
	}
	
	/**
	 * Sets the learning rate lambda
	 * @param l the learning rate
	 */
	public void setLambda(double l) {
		lambda = l;
	}

	/**
	 * Tells if this classifier is using a bias term
	 * @return true if a bias term is used
	 */
	public boolean isHasBias() {
		return hasBias;
	}

	/**
	 * Sets the use of a bias term
	 * @param hasBias true if use of bias term
	 */
	public void setHasBias(boolean hasBias) {
		this.hasBias = hasBias;
	}

	/**
	 * Tells the number of epochs this classifier uses for learning
	 * @return the number of epochs
	 */
	public int getEpochs() {
		return epochs;
	}

	/**
	 * Sets the number of epochs this classifier uses for learning
	 * @param epochs the number of epochs
	 */
	public void setEpochs(int epochs) {
		this.epochs = epochs;
	}

	/**
	 * Tells if samples are shuffled while learning
	 * @return true if samples are shuffled
	 */
	public boolean isShuffle() {
		return shuffle;
	}

	/**
	 * Sets if samples should be shuffled while learning
	 * @param shuffle true if shuffle
	 */
	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	/**
	 * Creates and returns a copy of this object.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public DoubleSGD copy() throws CloneNotSupportedException {
		return (DoubleSGD) super.clone();
	}

	/**
	 * Returns the hyper-parameter lambda
	 * @return lambda
	 */
	public double getLambda() {
		return lambda;
	}
}
