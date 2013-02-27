/**
    This file is part of JkernelMachines.

    JkernelMachines is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JkernelMachines is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JkernelMachines.  If not, see <http://www.gnu.org/licenses/>.

    Copyright David Picard - 2013

 */
package fr.lip6.jkernelmachines.test.evaluation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author picard
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ AccuracyEvaluatorTest.class, ApEvaluatorTest.class,
		MulticlassAccuracyEvaluatorTest.class,
		RandomSplitCrossValidationTest.class,
		LeaveOneOutCrossValidationTest.class, NFoldCrossValidationTest.class })
public class EvaluationTests {

}
