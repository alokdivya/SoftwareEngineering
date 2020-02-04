package com.hungarian.examples.hungarianProject;

import java.util.Arrays;
import java.util.Scanner;
import java.lang.*; 
import java.util.*; 

public class Hungarian
{

    double costMatrix [][]; 
    double labelByWorker[];
    double labelByJob[];
    double minSlackValueByJob[]; 
    int  rows, cols, dim; 
    int  minSlackWorkerByJob[]; 
    int  matchJobByWorker[];
    int  matchWorkerByJob[]; 
    int  parentWorkerByCommittedJob[]; 
    boolean  committedWorkers[];
    
    public Hungarian() {
    	
    }
    public Hungarian(double costMatrix [][] )
    {
    	if (costMatrix.length != costMatrix[0].length) {
            try {
                throw new IllegalAccessException("The matrix is not square!");
            } catch (IllegalAccessException ex) {
                System.err.println(ex);
                System.exit(1);
            }
        }
    	
        this.dim = Math.max(costMatrix.length, costMatrix[0].length);
        this.rows = costMatrix.length;
        this.cols = costMatrix[0].length;
        this.costMatrix = new double[this.dim][this.dim];

        for (int w = 0; w < this.dim; w++)
        {
            if (w < costMatrix.length)
            {

                this.costMatrix[w] = Arrays.copyOf(costMatrix[w], this.dim);
            }
            else
            {
                this.costMatrix[w] = new double[this.dim];
            }
        }

        labelByWorker = new double[this.dim];
        labelByJob = new double[this.dim];
        minSlackWorkerByJob = new int[this.dim];
        minSlackValueByJob = new double[this.dim];
        committedWorkers = new boolean[this.dim];
        parentWorkerByCommittedJob = new int[this.dim];
        
        matchJobByWorker = new int[this.dim];
        Arrays.fill(matchJobByWorker, -1);
        matchWorkerByJob = new int[this.dim];
        Arrays.fill(matchWorkerByJob, -1);
    }

 

    public void computeInitialFeasibleSolution()
    {
        for (int j = 0; j < dim; j++)
        {
            labelByJob[j] = Long.MAX_VALUE;
        }
        
        for (int w = 0; w < dim; w++)
        {
            for (int j = 0; j < dim; j++)
            {
                if (costMatrix[w][j] < labelByJob[j])
                {
                    labelByJob[j] = costMatrix[w][j];
                }
            }
        }
    }

    public int[] execute()
    {

        /*

         * Heuristics to improve performance: Reduce rows and columns by their

         * smallest element, compute an initial non-zero dual feasible solution

         * and

         * create a greedy matching from workers to jobs of the cost matrix.

         */

        reduce();
        computeInitialFeasibleSolution();
        greedyMatch();
        int w = fetchUnmatchedWorker();
        while (w < dim)
        {
            initializePhase(w);
            executePhase();
            w = fetchUnmatchedWorker();
        }

        int[] result = Arrays.copyOf(matchJobByWorker, rows);
        for (w = 0; w < result.length; w++)
        {
            if (result[w] >= cols)
            {
                result[w] = -1;
            }
        }
        return result;
    }

    public void executePhase()
    {
        while (true)
        {
            int minSlackWorker = -1, minSlackJob = -1;
            double minSlackValue = Long.MAX_VALUE;
            for (int j = 0; j < dim; j++)
            {
                if (parentWorkerByCommittedJob[j] == -1)
                {
                    if (minSlackValueByJob[j] < minSlackValue)
                    {
                        minSlackValue = minSlackValueByJob[j];
                        minSlackWorker = minSlackWorkerByJob[j];
                        minSlackJob = j;
                    }
                }
            }
            if (minSlackValue > 0)
            {
                updateLabeling(minSlackValue);
            }
            parentWorkerByCommittedJob[minSlackJob] = minSlackWorker;
            if (matchWorkerByJob[minSlackJob] == -1)
            {
                /*

                 * An augmenting path has been found.

                 */

                int committedJob = minSlackJob;
                int parentWorker = parentWorkerByCommittedJob[committedJob];
                while (true)
                {
                    int temp = matchJobByWorker[parentWorker];
                    match(parentWorker, committedJob);
                    committedJob = temp;
                    if (committedJob == -1)
                    {
                        break;
                    }
                    parentWorker = parentWorkerByCommittedJob[committedJob];
                }
                return;
            }
            else
            {

                /*

                 * Update slack values since we increased the size of the

                 * committed

                 * workers set.

                 */
                int worker = matchWorkerByJob[minSlackJob];
                committedWorkers[worker] = true;
                for (int j = 0; j < dim; j++)
                {
                    if (parentWorkerByCommittedJob[j] == -1)
                    {
                        double slack = costMatrix[worker][j]
                                - labelByWorker[worker] - labelByJob[j];
                        if (minSlackValueByJob[j] > slack)
                        {
                            minSlackValueByJob[j] = slack;
                            minSlackWorkerByJob[j] = worker;
                        }
                    }
                }
            }
        }
    }


    public int fetchUnmatchedWorker()
    {
        int w;
        for (w = 0; w < dim; w++)
        {
            if (matchJobByWorker[w] == -1)
            {
                break;
            }
        }
        return w;
    }


    public void greedyMatch()
    {
        for (int w = 0; w < dim; w++)
        {
            for (int j = 0; j < dim; j++)
            {
                if (matchJobByWorker[w] == -1
                        && matchWorkerByJob[j] == -1
                        && costMatrix[w][j] - labelByWorker[w] - labelByJob[j] == 0)
                {
                    match(w, j);
                }
            }
        }
    }

 

    public void initializePhase(int w)
    {
        Arrays.fill(committedWorkers, false);
        Arrays.fill(parentWorkerByCommittedJob, -1);
        committedWorkers[w] = true;

        for (int j = 0; j < dim; j++)
        {
            minSlackValueByJob[j] = costMatrix[w][j] - labelByWorker[w] - labelByJob[j];
            minSlackWorkerByJob[j] = w;
        }
    }

    public void match(int w, int j)
    {
        matchJobByWorker[w] = j;
        matchWorkerByJob[j] = w;
    } 

    public void reduce()
    {
        for (int w = 0; w < dim; w++)
        {
           double min = Long.MAX_VALUE;
            for (int j = 0; j < dim; j++)
            {
                if (costMatrix[w][j] < min)
                {
                    min = costMatrix[w][j];
                }
            }
            for (int j = 0; j < dim; j++)
            {
                costMatrix[w][j] -= min;
            }
        }

        double[] min = new double[dim];
        for (int j = 0; j < dim; j++)
        {
            min[j] = Long.MAX_VALUE;
        }

        for (int w = 0; w < dim; w++)
        {
            for (int j = 0; j < dim; j++)
            {
                if (costMatrix[w][j] < min[j])
                {
                    min[j] = costMatrix[w][j];
                }
            }
        }

        for (int w = 0; w < dim; w++)
        {
            for (int j = 0; j < dim; j++)
            {
                costMatrix[w][j] -= min[j];
            }
        }
    }
 

    public void updateLabeling(double slack)
    {
        for (int w = 0; w < dim; w++)
        {
            if (committedWorkers[w])
            {
                labelByWorker[w] += slack;
            }
        }

        for (int j = 0; j < dim; j++)
        {
            if (parentWorkerByCommittedJob[j] != -1)
            {
                labelByJob[j] -= slack;
            }

            else
            {
               minSlackValueByJob[j] -= slack;
            }
        }
    }

 
    public double alok(double [][] matrix)

    {
        int r = matrix.length;
        int c = matrix[0].length;
        double[][] cost = new double[r][c];
        for (int i = 0; i < r; i++)
        {
            for (int j = 0; j < c; j++)
            {
                cost[i][j] = matrix[i][j];
            }
        }

        Hungarian hbm = new Hungarian(cost);
        int[] result = hbm.execute();
        double total_cost=0;
        for (int j = 0; j < matrix.length; j++)
        {   
            total_cost+=matrix[j][result[j]];
            System.out.println("Cost of Worker: "+ (j+1)+" " + matrix[j][result[j]]);
        }
        System.out.println("total cost: " + total_cost+ "\n");
        return total_cost;
        
        
    }
}
