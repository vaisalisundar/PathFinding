package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        int values[][] = new int[8][8];
        int upperbound = 100;
        Random rand = new Random();


        for (int i = 0; i < values.length; i++) {
            // do the for in the row according to the column size
            for (int j = 0; j < values[i].length; j++) {
                int int_random = rand.nextInt(upperbound);
                if(i==0&&j==0 || i==7&&j==7){
                    values[i][j]=0;
                }else{
                    if(int_random<20){
                        values[i][j] = 1;
                    }else {
                        values[i][j] = 0;
                    }
                }

                System.out.print(values[i][j]);
            }
            // add a new line
            System.out.println();
        }
        System.out.println("Done");
        Solution sol = new Solution();
        int[][] grid = values;
        System.out.println(sol.shortestPathBinaryMatrix(grid));
    }
}

class Solution {

    // Candidate represents a possible option for travelling to the cell
    // at (row, col).
    class Candidate {

        public int row;
        public int col;
        public int distanceSoFar;
        public int totalEstimate;

        public Candidate(int row, int col, int distanceSoFar, int totalEstimate) {
            this.row = row;
            this.col = col;
            this.distanceSoFar = distanceSoFar;
            this.totalEstimate = totalEstimate;
        }
    }


    private static final int[][] directions =
            new int[][]{ {-1, 0}, {0, -1}, {0, 1}, {1, 0}};


    public int shortestPathBinaryMatrix(int[][] grid) {

        // Firstly, we need to check that the start and target cells are open.
        if (grid[0][0] != 0 || grid[grid.length - 1][grid[0].length - 1] != 0) {
            return -1;
        }

        // Set up the A* search.
        Queue<Candidate> pq = new PriorityQueue<>((a, b) -> a.totalEstimate - b.totalEstimate);
        pq.add(new Candidate(0, 0, 1, estimate(0, 0, grid)));

        boolean[][] visited = new boolean[grid.length][grid[0].length];

        // Carry out the A* search.
        while (!pq.isEmpty()) {

            Candidate best = pq.remove();

            // Is this for the target cell?
            if (best.row == grid.length - 1 && best.col == grid[0].length - 1) {
                return best.distanceSoFar;
            }

            // Have we already found the best path to this cell?
            if (visited[best.row][best.col]) {
                continue;
            }

            visited[best.row][best.col] = true;

            for (int[] neighbour : getNeighbours(best.row, best.col, grid)) {
                int neighbourRow = neighbour[0];
                int neighbourCol = neighbour[1];

                // This check isn't necessary for correctness, but it greatly
                // improves performance.
                if (visited[neighbourRow][neighbourCol]) {
                    continue;
                }

                // Otherwise, we need to create a Candidate object for the option
                // of going to neighbor through the current cell.
                int newDistance = best.distanceSoFar + 1;
                int totalEstimate = newDistance + estimate(neighbourRow, neighbourCol, grid);
                Candidate candidate =
                        new Candidate(neighbourRow, neighbourCol, newDistance, totalEstimate);
                pq.add(candidate);
            }
        }
        // The target was unreachable.
        return -1;
    }

    private List<int[]> getNeighbours(int row, int col, int[][] grid) {
        List<int[]> neighbours = new ArrayList<>();
        for (int i = 0; i < directions.length; i++) {
            int newRow = row + directions[i][0];
            int newCol = col + directions[i][1];
            if (newRow < 0 || newCol < 0 || newRow >= grid.length
                    || newCol >= grid[0].length
                    || grid[newRow][newCol] != 0) {
                continue;
            }
            neighbours.add(new int[]{newRow, newCol});
        }
        return neighbours;
    }

    // Get the best case estimate of how much further it is to the bottom-right cell.
    private int estimate(int row, int col, int[][] grid) {
        //Manhattan distance
        int heuristic = Math.abs(row-grid.length)+Math.abs(col-grid[0].length);
        return heuristic;
    }
}