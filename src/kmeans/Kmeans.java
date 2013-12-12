package kmeans;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Scanner;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Kmeans {
	
	public static int[][] label;
	
	public static IplImage findKmeans(IplImage myImage,int k){
		
		// Copy the myImage to myCopyImage
		IplImage myCopyImage = myImage.clone();
		
		int x = myImage.height();
		int y = myImage.width();
		
		label = new int[x][y];
				
		// Get the Image Pixels to a Buffer
		ByteBuffer buffer = myCopyImage.getByteBuffer();
		
		int[][] blueMatrix  = new int[x][y];
		int[][] greenMatrix = new int[x][y];
		int[][] redMatrix 	= new int[x][y];
					
		for(int i = 0; i < x; i++) {
		    for(int j = 0; j < y; j++) {
		        
		    	int index = i * myCopyImage.widthStep() + j * myCopyImage.nChannels();

		        // Used to read the pixel value - the 0xFF is needed to cast from
		        // an unsigned byte to an int.
		        blueMatrix[i][j]  =	buffer.get(index) & 0xFF;
		        greenMatrix[i][j] = buffer.get(index + 1) & 0xFF;
		        redMatrix[i][j]   = buffer.get(index + 2) & 0xFF;		        		        
		    }		    
		}
		
		int[] bluelist = new int[k];
		int[] greenlist= new int[k];
		int[] redlist  = new int[k];	
		
		Random color = new Random();
		
		System.out.println("\n B   G   R ");
		System.out.println("-----------");
		
		for(int i=0 ; i < k ;i++){		
			bluelist[i]  = color.nextInt(256);
			greenlist[i] = color.nextInt(256);
			redlist[i]   = color.nextInt(256);
			System.out.println(bluelist[i]+" "+ greenlist[i]+" "+redlist[i] +" ");
		}
		
		
		
		int a = 1;
		int iteration=0;
		
		//K-MEANS ALGORÝTHM
		while(a == 1 && iteration <1000){
			
			//Calculate Distance
			for(int i = 0; i < x; i++) {
			    for(int j = 0; j < y; j++) {
			    	
			    	int temp;
			    	int min	 =1000;
			    	
			    	for(int l = 0; l < k; l++) {			    		
			    					    		
			    		//euclidian distance
			    		temp  = Math.abs(bluelist[l]*bluelist[l] - blueMatrix[i][j]*blueMatrix[i][j]);
			    		temp += Math.abs(redlist[l]*redlist[l] - redMatrix[i][j]*redMatrix[i][j]);	
			    		temp += Math.abs(greenlist[l]*greenlist[l] - greenMatrix[i][j]*greenMatrix[i][j]);
			    		temp = (int) Math.sqrt(temp);
			    		
			    		if(temp<min){
			    			label[i][j] = l;
			    			min = temp;
			    		}
			    	}//end_if			    			    	
			    }//end_for	
			}//End_for	
			
			
			int[] sumBlue  = new int[k];
			int[] sumGreen = new int[k];
			int[] sumRed   = new int[k];	
			
			int[] sum = new int[k];
						
			for(int i = 0; i < x; i++) {
			    for(int j = 0; j < y; j++) {
			    	
			    	sum[label[i][j]] ++;
			    	
			    	sumBlue[label[i][j]]  += blueMatrix[i][j];
			    	sumGreen[label[i][j]] += greenMatrix[i][j];
			    	sumRed[label[i][j]]   += redMatrix[i][j];
			    }
			}//end_for
			
			int count=0;
			for(int l = 0; l < k; l++) {
				if(sum[l] != 0){
					if( bluelist [l] == sumBlue [l] / sum[l] && 
						greenlist[l] == sumGreen[l] / sum[l] &&
						redlist  [l] == sumRed  [l] / sum[l]    ){
						count++;}
					if(count==4){
						a=0;
					} 
				}
			}//end_for
			
			for(int l = 0; l < k; l++) {
				if(sum[l]!=0){				
					bluelist [l] = sumBlue [l] / sum[l];
					greenlist[l] = sumGreen[l] / sum[l];
					redlist  [l] = sumRed  [l] / sum[l];
				}
			}//end_for
			
			iteration++;
		}// end_while
		
		
		System.out.println("\n B   G   R ");
		System.out.println("-----------");

		
		for(int i=0 ; i < k ;i++){
			System.out.println(bluelist[i]+" "+ greenlist[i]+" "+redlist[i] +" ");
		}
		
		
		for(int i = 0; i < x; i++) {
		    for(int j = 0; j < y; j++) {
		        
		    	int index = i * myCopyImage.widthStep() + j * myCopyImage.nChannels();
        
		        // Sets the pixel to a value (RGB, stored in BGR order).
		        buffer.put(index,     (byte) bluelist [label[i][j]]);
		        buffer.put(index + 1, (byte) greenlist[label[i][j]]);
		        buffer.put(index + 2, (byte) redlist  [label[i][j]]);
		    }
		}//end_for		
		
		return myCopyImage;		
	}
	
	
	public static void main(String[] args) {
		
		System.out.print("k degeri : ");
		Scanner input = new Scanner(System.in);
		int k = input.nextInt();
		
		System.out.print("\nÝmages/name.jpg   -  name : ");
		String name =input.next();		
		
		// Load image as IplImage (IplImage = BGR renk uzayý)
		final IplImage myImage = cvLoadImage("images//"+name+".jpg");
		
		//get images from kmeans method
		IplImage myCopyImage = findKmeans(myImage,k);
				
		// create canvas frame
		final CanvasFrame myCanvas = new CanvasFrame("KMeans"); //title = myCanvas
		final CanvasFrame myCanvas2 = new CanvasFrame("CCL");
		
		// Show image in canvas frame
		myCanvas.showImage(myCopyImage);
		cvSaveImage(name+"_"+k+"_KMeans.jpg",myCopyImage);

		
		// save the same Image in a different name
		//cvSaveImage("lena_copy.jpg",myCopyImage);
		
		// CONNECTED COMPANENT LABELÝNG
		IplImage image = myCopyImage;
        IplImage grayImage = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 1);
        cvCvtColor(image, grayImage, CV_BGR2GRAY);
        
        
        CvMemStorage mem;
        CvSeq contours = new CvSeq();
        CvSeq ptr = new CvSeq();
        cvThreshold(grayImage, grayImage, 150, 255, CV_THRESH_BINARY);
        mem = cvCreateMemStorage(0);
        
        //myCanvas.showImage(grayImage);

        cvFindContours(grayImage, mem, contours, sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));

        Random rand = new Random();
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
            Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            CvScalar color = CV_RGB( randomColor.getRed(), randomColor.getGreen(), randomColor.getBlue());
            cvDrawContours(image, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
        }
        
		// Show image in canvas frame
		myCanvas2.showImage(image);
		
		cvSaveImage(name+"_"+k+"CCL.jpg",image);
		//cvSaveImage(name+"_"+k+"_KMeans.jpg",myCopyImage);
		
		// This will close canvas frame on exit
		myCanvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		//myCanvas2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}
}

