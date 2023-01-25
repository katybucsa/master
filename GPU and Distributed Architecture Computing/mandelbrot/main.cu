#define _CRT_SECURE_NO_DEPRECATE
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <sys/timeb.h>
#include "cuda_runtime.h"
#include "device_launch_parameters.h"

#include "tinycthread.c"
#include "util.c"

__constant__ unsigned char const_colormap[(MAX_ITERATION + 1) * 3];

cudaError_t generate_img(unsigned char* image);// , unsigned char* colormap);


__device__ void set_pixel(unsigned char* image, int width, int x, int y, unsigned char* c) {
	image[4 * width * y + 4 * x + 0] = c[0];
	image[4 * width * y + 4 * x + 1] = c[1];
	image[4 * width * y + 4 * x + 2] = c[2];
	image[4 * width * y + 4 * x + 3] = 255;
}

/* This should be conveted into a GPU kernel */
__global__ void generate_image(unsigned char* image, int const_width, int const_height, int const_max) {

	int row, col, index, iteration;
	double c_re, c_im, x, y, x_new;

	index = threadIdx.x + blockIdx.x * blockDim.x;

	while (index < const_width * const_height) {

		row = index / const_width;
		col = index % const_width;

		c_re = (col - const_width / 2.0) * 4.0 / const_width;
		c_im = (row - const_height / 2.0) * 4.0 / const_width;
		x = 0, y = 0;
		iteration = 0;
		while (x * x + y * y <= 4 && iteration < const_max) {
			x_new = x * x - y * y + c_re;
			y = 2 * x * y + c_im;
			x = x_new;
			iteration++;
		}
		if (iteration > const_max) {
			iteration = const_max;
		}
		set_pixel(image, const_width, col, row, &const_colormap[iteration * 3]);
		index += blockDim.x * gridDim.x;
	}
}

int main(int argc, char** argv) {
	double times[REPEAT];
	struct timeb start, end;
	int r;
	char path[] = "./";

	unsigned char* colormap = (unsigned char*)malloc((MAX_ITERATION + 1) * 3);
	unsigned char* image = (unsigned char*)malloc(WIDTH * HEIGHT * 4);

	cudaError_t cudaStatus;

	init_colormap(MAX_ITERATION, colormap);

	cudaStatus = cudaMemcpyToSymbol(const_colormap, colormap, (MAX_ITERATION + 1) * 3, 0, cudaMemcpyHostToDevice);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "Copy colormap to constant memory from device to host failed!");
		free(image);
		free(colormap);
		return 1;
	}
	free(colormap);


	for (r = 0; r < REPEAT; r++) {
		memset(image, 0, WIDTH * HEIGHT * 4);

		ftime(&start);

		cudaStatus = generate_img(image); // , colormap);
		if (cudaStatus != cudaSuccess) {
			fprintf(stderr, "generate_img failed!");
			free(image);
			free(colormap);
			return 1;
		}

		ftime(&end);
		times[r] = end.time - start.time + ((double)end.millitm - (double)start.millitm) / 1000.0;

		sprintf(path, IMAGE, "gpu", r);
		save_image(path, image, WIDTH, HEIGHT);
		progress("gpu", r, times[r]);
	}
	report("gpu", times);

	free(image);
	//free(colormap);

	//cudaDeviceReset must be  called before exiting in order for profiling and
	// tracing tools such as Nsight and Visual Profiler to show complete traces.
	cudaStatus = cudaDeviceReset();
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaDeviceReset failed!");
		return 1;
	}
	return 0;
}


cudaError_t generate_img(unsigned char* image){ //, unsigned char* colormap) {

	unsigned char* dev_image;// , * dev_colormap;
	cudaError_t cudaStatus;


	cudaStatus = cudaSetDevice(0);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaSetDevice failed!");
		goto Error;
	}

	// Allocate GPU buffer for image
	/*cudaStatus = cudaMalloc((void**)&dev_colormap, (MAX_ITERATION + 1) * 3);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMalloc failed!");
		goto Error;
	}*/

	cudaStatus = cudaMalloc((void**)&dev_image, WIDTH * HEIGHT * 4);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMalloc failed!");
		goto Error;
	}


	/*cudaStatus = cudaMemcpy(dev_colormap, colormap, (MAX_ITERATION + 1) * 3, cudaMemcpyHostToDevice);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMemcpy failed!");
		goto Error;
	}*/

	generate_image<<<BLOCKS, THREADS>>>(dev_image, WIDTH, HEIGHT, MAX_ITERATION);

	// Errors when lunching the kernel
	cudaStatus = cudaGetLastError();
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "Failed to generate image: %s\n", cudaGetErrorString(cudaStatus));
		goto Error;
	}

	// cudaDeviceSynchronize waits for the kernel to finish and returns
	// any errors encountered during the launch.
	cudaStatus = cudaDeviceSynchronize();
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaDeviceSynchronize returned error code %d after launching generate_image!\n", cudaStatus);
		goto Error;
	}

	// Copy image from GPU buffer to host memory
	cudaStatus = cudaMemcpy(image, dev_image, WIDTH * HEIGHT * 4, cudaMemcpyDeviceToHost);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "Copy image from device to host failed!");
		goto Error;
	}

Error:
	cudaFree(dev_image);
	//cudaFree(dev_colormap);
	return cudaStatus;
}
