package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws NullPointerException{

        List<Double> pData = new ArrayList<>();
        List<Integer> lengthData = new ArrayList<>();
        List<Integer> cellsProcessed = new ArrayList<>();
        List<Integer> cellsProcessedRFA = new ArrayList<>();
        List<Boolean> solvabilty = new ArrayList<>();
        for(int l=0;l<1;l++){
            int dim = 8;
            int values[][] = new int[dim][dim];
            int dummyValues[][] = new int[dim][dim];

            double p =  Math.random();
            while(p>0.33){
                p = Math.random();
            }

            //creating a dummy grid with no blocked cells
            for(int rn=0;rn<dummyValues.length;rn++){
                for(int cl=0;cl<dummyValues[rn].length;cl++){
                    dummyValues[rn][cl]=0;
                    System.out.print(dummyValues[rn][cl]);
                }
                System.out.println();
            }
            System.out.println("Done printing the dummy grid");

            for (int i = 0; i < values.length; i++) {
                // do the for in the row according to the column size
                for (int j = 0; j < values[i].length; j++) {

                    if(i==0&&j==0 || i==dim-1&&j==dim-1){
                        values[i][j]=0;
                    }else{
                        if(Math.random()>=p){
                            values[i][j] = 0;
                        }else {
                            values[i][j] = 1;
                        }
                    }

                    System.out.print(values[i][j]);
                }
                // add a new line
                System.out.println();
            }
            System.out.println("Done Printing the actual grid");

            Solution sol = new Solution();
            //grid is the actual maze
            int[][] grid = values;

            //newGrid is the maze with no blockage
            int[][] newGrid = dummyValues;


            int length = sol.shortestPathBinaryMatrix(grid);
            System.out.println(length);
            System.out.println();

            //output from repeated forward A*
            if(length!=-1) {
                int rfAstarLength = sol.repeatedForwardAstar(newGrid, 0, 0, 1, grid);
                System.out.println(rfAstarLength);
            }
            //To check Solvabilty
            if(length!=-1){
                //If the maze is solvable adding true to the solvability list
                solvabilty.add(true);
            }else{
                //If the maze is not solvable adding false to the solvability list
                solvabilty.add(false);
            }

            int cells = sol.cellsProcessed()-1;
            //System.out.println(cells);

            //No.of.cells processed in RFA
            int cellsRFA = sol.cellsProcessedRFA()-1;

            pData.add(p);
            lengthData.add(length);
            cellsProcessed.add(cells);
            cellsProcessedRFA.add(cellsRFA);



        }
        FileWriter locFile1 = null;
        FileWriter locFile2 = null;
        FileWriter locFile3 = null;
        FileWriter locFile4 = null;
        FileWriter locFile5 = null;

        try{
            locFile1 = new FileWriter("pData.txt");
            locFile2 = new FileWriter("lengthData.txt");
            locFile3 = new FileWriter("cellsProcessed.txt");
            locFile4 = new FileWriter("solvability.txt");
            locFile5 = new FileWriter("cellProcessedRFA.txt");


            for(int i = 0;i<pData.size();i++){
                locFile1.write(pData.get(i)+"\n");
                locFile2.write(lengthData.get(i)+"\n");
                locFile3.write(cellsProcessed.get(i)+"\n");
                locFile4.write(solvabilty.get(i)+"\n");
                locFile5.write(cellsProcessedRFA.get(i)+"\n");
            }
            locFile5.close();
            locFile4.close();
            locFile3.close();
            locFile2.close();
            locFile1.close();
        }catch (IOException e){
            System.out.println("In catch block");
            e.printStackTrace();
        }




    }
}

class Solution {
    Map<List<Integer>,List<Integer>> pathMapRFA = new HashMap<List<Integer>,List<Integer>>();

    boolean[][] visitedRFA = new boolean[8][8];
    //Queue<Candidate> pqRFA = new PriorityQueue<>((a, b) -> a.totalEstimate - b.totalEstimate);
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
        Map<List<Integer>,List<Integer>> pathMap = new HashMap<List<Integer>,List<Integer>>();


        // Carry out the A* search.
        while (!pq.isEmpty()) {

            Candidate best = pq.remove();
            int counter = cellsProcessed();


            // Is this for the target cell?
            if (best.row == grid.length - 1 && best.col == grid[0].length - 1) {

                List<Integer> goalnode = new ArrayList<>();
                goalnode.add(best.row);
                goalnode.add(best.col);
                System.out.print(goalnode);
                System.out.print("->");
                List<Integer> result = pathMap.get(goalnode);
                for(int i=0;i<best.distanceSoFar;i++){
                    System.out.print(result);
                    System.out.print("->");
                    result = pathMap.get(result);

                }

                return best.distanceSoFar;
            }

            // Have we already found the best path to this cell?
            if (visited[best.row][best.col]) {
                continue;
            }

            visited[best.row][best.col] = true;
           // pathQueue.add({new int[]{best.row,best.col},{});

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
                List<Integer> neigh = new ArrayList<>();
                neigh.add(neighbourRow);
                neigh.add(neighbourCol);
                List<Integer> parent = new ArrayList<>();
                parent.add(best.row);
                parent.add(best.col);
                if(pathMap.containsKey(neigh)){
                    continue;
                }else {
                    pathMap.put(neigh, parent);
                }


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
        int heuristic = Math.abs(row-(grid.length-1))+Math.abs(col-(grid[0].length-1));
        return heuristic;
    }
    public static int cellsProcessed = 0;
    public int cellsProcessed(){
       return cellsProcessed++;
    }

    public static int cellsProcessedRFA = 0;
    public int cellsProcessedRFA(){
        return cellsProcessedRFA++;
    }


    public int repeatedForwardAstar(int[][] newGrid,int row, int col,int distanceSoFar,int[][] grid) {

        // Firstly, we need to check that the start and target cells are open.
        if (newGrid[0][0] != 0 || newGrid[newGrid.length - 1][newGrid[0].length - 1] != 0) {
            return -1;
        }

        // Set up the A* search.
        Queue<Candidate> pqRFA = new PriorityQueue<>((a, b) -> a.totalEstimate - b.totalEstimate);
        pqRFA.add(new Candidate(row, col, distanceSoFar, estimate(row, col, newGrid)));

        //boolean[][] visited = new boolean[newGrid.length][newGrid[0].length];
       // Map<List<Integer>,List<Integer>> pathMap = new HashMap<List<Integer>,List<Integer>>();


        // Carry out the A* search.
        while (!pqRFA.isEmpty()) {

            Candidate best = pqRFA.remove();
            int counter = cellsProcessedRFA();
            if(newGrid[best.row][best.col]!=grid[best.row][best.col]){
                newGrid[best.row][best.col]=grid[best.row][best.col];
                List<Integer> block = new ArrayList<>();
                block.add(best.row);
                block.add(best.col);
                List<Integer> parent = pathMapRFA.get(block);
                pathMapRFA.remove(block);
                visitedRFA[parent.get(0)][parent.get(1)] = false;
                int ans = repeatedForwardAstar(newGrid,parent.get(0),parent.get(1), best.distanceSoFar-1,grid);
                if(ans==-1){
                    best.row = parent.get(0);
                    best.col=parent.get(1);
                    visitedRFA[parent.get(0)][parent.get(1)] = false;
                }else{
                    return ans;
                }

            }



            // Is this for the target cell?
            if (best.row == newGrid.length - 1 && best.col == newGrid[0].length - 1) {

                List<Integer> goalnode = new ArrayList<>();
                goalnode.add(best.row);
                goalnode.add(best.col);
                System.out.print(goalnode);
                System.out.print("->");
                List<Integer> result = pathMapRFA.get(goalnode);
                for(int i=0;i<best.distanceSoFar;i++){
               // while(result.get(0)!=row && result.get(1)!=col){
                    System.out.print(result);
                    System.out.print("->");
                    result = pathMapRFA.get(result);

                }

                return best.distanceSoFar;
            }

            // Have we already found the best path to this cell?
            if (visitedRFA[best.row][best.col]) {
                continue;
            }

            visitedRFA[best.row][best.col] = true;


            for (int[] neighbour : getNeighbours(best.row, best.col, newGrid)) {
                int neighbourRow = neighbour[0];
                int neighbourCol = neighbour[1];

                // This check isn't necessary for correctness, but it greatly
                // improves performance.
                if (visitedRFA[neighbourRow][neighbourCol]) {
                    continue;
                }
                List<Integer> neigh = new ArrayList<>();
                neigh.add(neighbourRow);
                neigh.add(neighbourCol);
                List<Integer> parent = new ArrayList<>();
                parent.add(best.row);
                parent.add(best.col);


                // Otherwise, we need to create a Candidate object for the option
                // of going to neighbor through the current cell.
                int newDistance = best.distanceSoFar + 1;
                int totalEstimate = newDistance + estimate(neighbourRow, neighbourCol, newGrid);
                Candidate candidate =
                        new Candidate(neighbourRow, neighbourCol, newDistance, totalEstimate);
                if(pathMapRFA.containsKey(neigh)){
                    continue;
                }else{
                    pqRFA.add(candidate);
                }

//                List<Integer> neigh = new ArrayList<>();
//                neigh.add(neighbourRow);
//                neigh.add(neighbourCol);
//                List<Integer> parent = new ArrayList<>();
//                parent.add(best.row);
//                parent.add(best.col);
                if(pathMapRFA.containsKey(neigh)){
                    continue;
                }
                pathMapRFA.put(neigh, parent);



            }
        }
        // The target was unreachable.
        return -1;
    }
}