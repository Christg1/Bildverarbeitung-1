// BV Ue1 SS2023 Vorgabe
//
// Copyright (C) 2023 by Klaus Jung
// All rights reserved.
// Date: 2023-03-23
 		   	  	  		

package bv_ss23;

public class GaussFilter {
    
    private double[][] kernel;
    
    public double[][] getKernel() {
        return kernel;
    }
    
    public void apply(RasterImage src, RasterImage dst, int kernelSize, double sigma) {
        // Step 1: Allocate appropriate memory for the field variable "kernel" representing a 2D array.
        kernel = new double[kernelSize][kernelSize];
        
        // Step 2: Fill in appropriate values into the "kernel" array.
        double sum = 0;
        int halfSize = kernelSize / 2; //find the center of the kernel
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                double val = Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                kernel[x + halfSize][y + halfSize] = val; //assigns the pixel value "val" to a specific location in the kernel. x+halfSize represents the
                											// actual position of the pixel in the kernel
                sum += val;	//updates the "sum" with the newly added pixel value. Sum is later used to normalize the kernel:
            }
        }
        // Step 3: Normalize the "kernel" array such that its elements sum up to 1.
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                kernel[i][j] /= sum;
            }
        }
        
        // Step 4: Apply the filter by convolving the kernel with the source image "src".
        int width = src.width;
        int height = src.height;
        int[] srcPixels = src.argb; //declares and int array and initializes it with the ARGB pixel data of the source image src.
        int[] dstPixels = new int[srcPixels.length]; //declares an int array called dstPixels and initializes it with the same length as srcPixels. This
        											//array will hold the output pixel data after the image processing has been applied to the input.
        int kCenter = kernelSize / 2;				//find center of kernel
        int r, g, b, a;								//rgba storage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = g = b = a = 0;					//sets all pixels to rgba value of 0.
                for (int ky = 0; ky < kernelSize; ky++) { 
                    for (int kx = 0; kx < kernelSize; kx++) { //loops over each row and column of the kernel matrix
                        int srcX = x + kx - kCenter;
                        int srcY = y + ky - kCenter; //calculate the coordinates of the corresponding pixel in the source image that will be used for the 
                        							//current pixel being filtered in the destination image. The current pixel being filtered in the dest.
                        //image is located at x,y and the kernel center is a kCenter, so we use the current pixels coordinates x,y and the kernel center 
                        //coordinates kCenter to calculate the corresponding pixels coordinates in the source image.
                        if (srcX >= 0 && srcX < width && srcY >= 0 && srcY < height) { //checks if the calculated srcX and srcY are within the bounds 
                        									//of the source image src. 
                            int srcPixel = srcPixels[srcY * width + srcX]; //retrieves the ARGB value of the pixel at srcX,srcY. 
                            double weight = kernel[ky][kx]; //Weight is obtained from the kernel at current position ky,kx,
                            r += (int) (weight * ((srcPixel >> 16) & 0xff)); //calculate new pixel values for the dest. image. Weight is the kernel coefficient.
                            g += (int) (weight * ((srcPixel >> 8) & 0xff)); // & 0xff extracts the value of the red channel from srcPixel integer value.
                            b += (int) (weight * (srcPixel & 0xff));
                            a += (int) (weight * ((srcPixel >> 24) & 0xff));
                        }
                    }
                }
                dstPixels[y * width + x] = (a << 24) | (r << 16) | (g << 8) | b; //sets filtered pixel value in the output img dstPixels at the position x,y 
            } //using bitwise operations. combined using bitwise OR operator and assigned to the corresponding pixel in dstPixels at position x,y.
        }
        // Step 5: Set the result pixels to the destination image "dst".
        
        dst.argb = dstPixels;
    }
    
}
