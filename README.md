JKernelMachines: A simple framework for Kernel Machines
-------------------------------------------------------

JKernelMachines is a java library for learning with kernels. It is primary 
designed to deal with custom kernels that are not easily found in standard 
libraries, such as kernels on structured data.

This program is free software under the terms of the 3-clause BSD license.
See the COPYING file for more details.

Copyright David Picard 2016

picard@ensea.fr

## Features

* Several learning algorithms (LaSVM, LaSVM-I, SMO, SimpleMKL, GradMKL, QNPKL, SGDQN, Pegasos, NystromSVM, LLSVM...)
* Multiclass classification through generic classifiers.
* Active learning
* Datatype agnosticism through Java Generics
* Easy coding of new kernels
* Several standard and exotic kernels (kernel on bags, combination kernels, ...)
* Input system (can read libsvm, csv, arff and fvec files)
* Toys generator for artificial data
* Basic linear algebra package (optionally based on [EJML](http://code.google.com/p/efficient-java-matrix-library/))
* Evaluation and Cross Validation packages
* Stand alone (requires only a working jdk 1.7 and maven for easy compiling)
* Simple GUI

## HowTo

* [Compiling](https://github.com/davidpicard/jkernelmachines/wiki/Compiling)
* [Graphical User Interface](https://github.com/davidpicard/jkernelmachines/wiki/JkmsGUI)
* [Basic Example](https://github.com/davidpicard/jkernelmachines/wiki/Basic-Example)
* [Kernels](https://github.com/davidpicard/jkernelmachines/wiki/Kernel-HowTo)
* [Classifier Training](https://github.com/davidpicard/jkernelmachines/wiki/Classifier-Training)
* [Transductive classifiers](https://github.com/davidpicard/jkernelmachines/wiki/Transductive-Classifiers)
* [Multiclass Training](https://github.com/davidpicard/jkernelmachines/wiki/Multiclass)
* [Reading data](https://github.com/davidpicard/jkernelmachines/wiki/Reading-Data)
* [Data generators](https://github.com/davidpicard/jkernelmachines/wiki/Data-Generators)
* [Basic Linear Algebra](https://github.com/davidpicard/jkernelmachines/wiki/Basic-Linear-Algebra)
* [Evaluation](https://github.com/davidpicard/jkernelmachines/wiki/Evaluation)
* [CrossValidation](https://github.com/davidpicard/jkernelmachines/wiki/CrossValidation)
* [Standalone programs](https://github.com/davidpicard/jkernelmachines/wiki/Standalone-programs)

## Javadoc
Available with maven, or [here](http://davidpicard.github.io/jkernelmachines/doc/)


## FAQ
frequently asked questions are answered [here](https://github.com/davidpicard/jkernelmachines/wiki/FAQ)


Acknowledgement
---------------

This work was started while working at Lip6 - http://www.lip6.fr


