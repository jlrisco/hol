/*
* File: SBXCrossover.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/09/13 (YYYY/MM/DD)
*
* Copyright (C) 2010
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package jeco.core.operator.crossover;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * Class for simulated binary crossover.
 * 
 * This class implements the simulated binary crossover operator. The simulated binary
 * crossover operator is a genetic operator that combines two parents to generate two
 * offsprings. The operator is applied to the variables of the solutions.
 */
public class SBXCrossover<V extends Variable<Double>>  extends CrossoverOperator<V> {

    /** DEFAULT_ETA_C defines a default index crossover */
    public static final double DEFAULT_ETA_C = 20.0;
    public static final double DEFAULT_PROBABILITY = 0.9;
    /** EPS defines the minimum difference allowed between real values */
    private static final double EPS = 1.0e-14;
    /** eta_c stores the index for crossover to use */
    protected double eta_c;
    /** probability stores the probability of crossover */
    protected double probability;
    /** problem stores the problem */
    protected Problem<V> problem;

    /**
     * Constructor
     * @param problem Problem
     * @param eta_c Index for crossover
     * @param probability Probability of crossover
     */
    public SBXCrossover(Problem<V> problem, double eta_c, double probability) {
        this.problem = problem;
        this.eta_c = eta_c;
        this.probability = probability;
    }  // SBXCrossover

    /**
     * Constructor
     * @param problem Problem
     */
    public SBXCrossover(Problem<V> problem) {
        this(problem, DEFAULT_ETA_C, DEFAULT_PROBABILITY);
    } // SBXCrossover

    /**
     * Executes the crossover operation
     * @param probability Probability of crossover
     * @param parent1 First parent
     * @param parent2 Second parent
     * @return An object containing the offsprings
     */
    public Solutions<V> doCrossover(double probability, Solution<V> parent1, Solution<V> parent2) {

        Solutions<V> offSpring = new Solutions<V>();

        offSpring.add(parent1.clone());
        offSpring.add(parent2.clone());

        int i;
        double rand;
        double y1, y2, yL, yU;
        double c1, c2;
        double alpha, beta, betaq;
        double valueX1, valueX2;
        if (RandomGenerator.nextDouble() <= probability) {
            for (i = 0; i < parent1.getVariables().size(); i++) {
                valueX1 = parent1.getVariables().get(i).getValue();
                valueX2 = parent2.getVariables().get(i).getValue();
                if (RandomGenerator.nextDouble() <= 0.5) {

                    if (java.lang.Math.abs(valueX1 - valueX2) > EPS) {

                        if (valueX1 < valueX2) {
                            y1 = valueX1;
                            y2 = valueX2;
                        } else {
                            y1 = valueX2;
                            y2 = valueX1;
                        } // if

                        yL = problem.getLowerBound(i);
                        yU = problem.getUpperBound(i);
                        rand = RandomGenerator.nextDouble();
                        beta = 1.0 + (2.0 * (y1 - yL) / (y2 - y1));
                        alpha = 2.0 - java.lang.Math.pow(beta, -(eta_c + 1.0));

                        if (rand <= (1.0 / alpha)) {
                            betaq = java.lang.Math.pow((rand * alpha), (1.0 / (eta_c + 1.0)));
                        } else {
                            betaq = java.lang.Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (eta_c + 1.0)));
                        } // if

                        c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
                        beta = 1.0 + (2.0 * (yU - y2) / (y2 - y1));
                        alpha = 2.0 - java.lang.Math.pow(beta, -(eta_c + 1.0));

                        if (rand <= (1.0 / alpha)) {
                            betaq = java.lang.Math.pow((rand * alpha), (1.0 / (eta_c + 1.0)));
                        } else {
                            betaq = java.lang.Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (eta_c + 1.0)));
                        } // if

                        c2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));

                        if (c1 < yL) {
                            c1 = yL;
                        }

                        if (c2 < yL) {
                            c2 = yL;
                        }

                        if (c1 > yU) {
                            c1 = yU;
                        }

                        if (c2 > yU) {
                            c2 = yU;
                        }

                        if (RandomGenerator.nextDouble() <= 0.5) {
                            offSpring.get(0).getVariables().get(i).setValue(c2);
                            offSpring.get(1).getVariables().get(i).setValue(c1);
                        } else {
                            offSpring.get(0).getVariables().get(i).setValue(c1);
                            offSpring.get(1).getVariables().get(i).setValue(c2);
                        } // if
                    } else {
                        offSpring.get(0).getVariables().get(i).setValue(valueX1);
                        offSpring.get(1).getVariables().get(i).setValue(valueX2);
                    } // if
                } else {
                    offSpring.get(0).getVariables().get(i).setValue(valueX2);
                    offSpring.get(1).getVariables().get(i).setValue(valueX1);
                } // if
            } // if
        } // if
        return offSpring;
    } // doCrossover

    /**
     * Executes the crossover operation
     * @param parent1 First parent
     * @param parent2 Second parent
     * @return An object containing the offsprings
     */
    public Solutions<V> execute(Solution<V> parent1, Solution<V> parent2) {
        return doCrossover(probability, parent1, parent2);
    } // execute
} // SBXCrossover

