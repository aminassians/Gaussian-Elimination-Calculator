package gaussianElimination;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Stack;
import java.util.Scanner;

public class GaussianElimination {
    
    int numOfEq;
    float[][] matrix;
    float[][] originalMatrix;
    Scanner scan;
  
    
    public GaussianElimination() throws FileNotFoundException{
        scan = new Scanner(System.in);
        numOfEq = getNumOfEq();
        scan.nextLine(); 
        matrix = getMatrix();
        originalMatrix = Arrays.copyOf(matrix, matrix.length);
    } // end constructor
  

    private int getNumOfEq() {
        System.out.print("How many equations do you have? ");
        return scan.nextInt();
    } // end getNumOfEq
  

    private float[][] getMatrix() throws FileNotFoundException {
        matrix = new float[numOfEq][numOfEq+1];
        int inputChoice;
      
        System.out.print("Do you want to enter the coefficients of the equationa"
                + "or you want to put the filename? please enter 1 for entering and enter 2 for using filename.");
        inputChoice = scan.nextInt();
        scan.nextLine();
        if(inputChoice == 1) enterCoeff();
        else enterFile();      
        return matrix;
    } // end getMatrix
  

    private void enterCoeff(){
        String[] coefficients;

        for (int i = 0; i < numOfEq; i++) {
            System.out.print("Enter the coefficients of row " + (i+1) + " including the b value. "
                    + "Please seperate them with space.");
            coefficients = scan.nextLine().split("\\s+");

            for(int j = 0; j < coefficients.length; j++){
                matrix[i][j] = Float.parseFloat(coefficients[j]);
            }
        }
    } // end enterCoeff
  

    private void enterFile() throws FileNotFoundException {
        System.out.print("Enter the filename including extension: ");
        File file = new File(scan.nextLine());
        try (Scanner fileScanner = new Scanner(file)) {
        
            for (int i = 0; i < numOfEq && fileScanner.hasNextFloat();
                    i++) {
                
                for(int j = 0; j < numOfEq+1 &&
                        fileScanner.hasNextFloat(); j++){
                    matrix[i][j] = fileScanner.nextFloat();
                }
            }
            fileScanner.close();
        }
        catch(FileNotFoundException e){}
    } // end enterFile
  

    private void pivoting(float[][] matrix){
        
        float[] weights = getWeights(matrix);
        
        int pivotRow;
        float[] results = new float[numOfEq];
        
        Arrays.fill(results, 0);      
        
        Stack<Integer> pivotRecord = new Stack<>();
      
        for(int i = 0; i < numOfEq; i++){
            
            pivotRow = getPivotRow(i,weights,pivotRecord,matrix);
            
            gaussElimination(i,pivotRow,weights,pivotRecord,matrix);
        }
    
        backward(matrix,pivotRecord,results);
        printResults(results);
    } // end pivoting
  
   
    
    private float[] getWeights(float[][] matrix) {
        
        float[] weights = new float[numOfEq];
        float maximum;
       
        for(int i = 0; i < numOfEq; i++) {
            maximum = -1;
            for(int j = 0; j < numOfEq+1; j++) {
                maximum = Math.max(maximum, Math.abs(matrix[i][j]));
            }
            
            weights[i] = maximum;
        }
        return weights;
    } // end getWeights
  
  
    private int getPivotRow(int i, float[] weights, Stack<Integer> pivotRecord,
            float[][] matrix){
        
        int pivotRow;
        float[][] maxRatioPair = { {-1, -1} }; 
        float ratio;
       
        for(int j = 0; j < numOfEq; j++){
            
            if(!pivotRecord.contains(j)) {
                
                if(i+1 == numOfEq) {
                    pivotRecord.push(j);
                    break;
                }
                else {
                    
                    ratio = Math.abs(matrix[j][i] / weights[j]);
                   
                    if(ratio > maxRatioPair[0][1]) {
                        maxRatioPair[0][0] = j;
                        maxRatioPair[0][1] = ratio;
                    }
                }
            }
        }
        
        if(i+1 != numOfEq)
        {
            pivotRow = (int) maxRatioPair[0][0];
            pivotRecord.push(pivotRow);  
        }
        else pivotRow = pivotRecord.peek();
        return pivotRow;
    } 
  

    private void gaussElimination(int i, int pivotRow,float[] weights,
            Stack<Integer> pivotRecord, float[][] matrix){
        float pivotNumber;
        float pivotRate;
        float targetNumber;
        float pivotWeight;
        
        if(i+1 != numOfEq) {
            for(int j = 0; j < numOfEq; j++) {
                if((!pivotRecord.contains(j)) || matrix[j][i] == 0) {
                    pivotRate = matrix[pivotRow][i] / matrix[j][i];
                    
                    pivotWeight = weights[pivotRow];
                   
                    for(int k = 0; k < numOfEq+1; k++) {
                        pivotNumber = matrix[pivotRow][k];
                        targetNumber = matrix[j][k];
                        matrix[j][k] = (pivotNumber - pivotRate * targetNumber) / pivotWeight;
                    }
                    printMatrix(matrix);
                }
            }
        }
    } 
  

    private void backward(float[][] matrix, Stack<Integer> pivotRecord,
            float[] results){      
        int pivotRow;
        float tempWeight;
        for(int i = numOfEq-1; !pivotRecord.empty();i--) {
            pivotRow = pivotRecord.pop();
            tempWeight = matrix[pivotRow][i];
            results[i] = matrix[pivotRow][numOfEq] / tempWeight;

            for(int j = numOfEq-1; j >= 0 ; j--) {
                if(i != j)
                {
                    results[i] -= (matrix[pivotRow][j] / tempWeight) * results[j];
                }
            }
        }
    } // end backward
  

    public void printResults(float[] results){
        DecimalFormat df = new DecimalFormat("#.###");

        for(int i = 0; i < results.length; i++){
            System.out.println("x" + (i+1) + " = " + df.format(results[i]));
        }
        System.out.println("");
    } // end print results;
  

    public static void printMatrix(float[][] matrix){

        DecimalFormat df = new DecimalFormat("#.###");
        
        for (float[] row : matrix) {
            System.out.print("[ ");
            for (int column = 0; column < row.length; column++) {
                System.out.print(df.format(row[column]) + " ");
            }
            System.out.println("]");
        }
        System.out.println("");
    } // end printMatrix      
          
    public static void cloneMatrix(float[][] target, float[][] source){
        for(int i = 0; i < source.length; i++)
            target[i] = source[i].clone();
    }
  

    public static void main(String[] args) throws FileNotFoundException {
        GaussianElimination gauss = new GaussianElimination();
        float[][] myMatrix = gauss.matrix;
        float[][] originalMatrix = new float[myMatrix.length][myMatrix[0].length];
        cloneMatrix(originalMatrix, myMatrix);
        GaussianElimination.printMatrix(originalMatrix);
      
        System.out.println("Gaussian Elimination:");
        gauss.pivoting(originalMatrix);
        cloneMatrix(originalMatrix, myMatrix);
      
    } // end main
  
} // end GaussianElimination


