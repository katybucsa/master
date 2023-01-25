#define _CRT_SECURE_NO_DEPRECATE

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <sys/timeb.h>
#include "common.h"

/*
 * SHA-1 GPU implementation
 */
typedef struct {
    unsigned long total[2];     /* number of bytes processed  */
    unsigned long state[5];     /* intermediate digest state  */
    unsigned char buffer[64];   /* data block being processed */
} sha1_gpu_context;


__device__ static const unsigned char sha1_padding[64] =
        {
                0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        };


/*
 * Prepare SHA-1 for execution.
 */
__device__ void sha1_gpu_starts(sha1_gpu_context *ctx) {
    ctx->total[0] = 0;
    ctx->total[1] = 0;
    ctx->state[0] = 0x67452301;
    ctx->state[1] = 0xEFCDAB89;
    ctx->state[2] = 0x98BADCFE;
    ctx->state[3] = 0x10325476;
    ctx->state[4] = 0xC3D2E1F0;
}


/*
 * Process one block of data.
 */
__device__ static void sha1_gpu_process(sha1_gpu_context *ctx, unsigned char data[64]) {
    unsigned long temp, W[16] = {0,}, A, B, C, D, E;

    GET_UINT32_BE(W[0], data, 0);
    GET_UINT32_BE(W[1], data, 4);
    GET_UINT32_BE(W[2], data, 8);
    GET_UINT32_BE(W[3], data, 12);
    GET_UINT32_BE(W[4], data, 16);
    GET_UINT32_BE(W[5], data, 20);
    GET_UINT32_BE(W[6], data, 24);
    GET_UINT32_BE(W[7], data, 28);
    GET_UINT32_BE(W[8], data, 32);
    GET_UINT32_BE(W[9], data, 36);
    GET_UINT32_BE(W[10], data, 40);
    GET_UINT32_BE(W[11], data, 44);
    GET_UINT32_BE(W[12], data, 48);
    GET_UINT32_BE(W[13], data, 52);
    GET_UINT32_BE(W[14], data, 56);
    GET_UINT32_BE(W[15], data, 60);

#define S(x, n) ((x << n) | ((x & 0xFFFFFFFF) >> (32 - n)))

#define R(t)                        \
(                                                       \
    temp = W[(t -  3) & 0x0F] ^ W[(t - 8) & 0x0F] ^     \
           W[(t - 14) & 0x0F] ^ W[ t      & 0x0F],      \
    ( W[t & 0x0F] = S(temp,1) )                         \
)

#define P(a, b, c, d, e, x)                                  \
{                                                       \
    e += S(a,5) + F(b,c,d) + K + x; b = S(b,30);        \
}

    A = ctx->state[0];
    B = ctx->state[1];
    C = ctx->state[2];
    D = ctx->state[3];
    E = ctx->state[4];

#define F(x, y, z) (z ^ (x & (y ^ z)))
#define K 0x5A827999

    P(A, B, C, D, E, W[0]);
    P(E, A, B, C, D, W[1]);
    P(D, E, A, B, C, W[2]);
    P(C, D, E, A, B, W[3]);
    P(B, C, D, E, A, W[4]);
    P(A, B, C, D, E, W[5]);
    P(E, A, B, C, D, W[6]);
    P(D, E, A, B, C, W[7]);
    P(C, D, E, A, B, W[8]);
    P(B, C, D, E, A, W[9]);
    P(A, B, C, D, E, W[10]);
    P(E, A, B, C, D, W[11]);
    P(D, E, A, B, C, W[12]);
    P(C, D, E, A, B, W[13]);
    P(B, C, D, E, A, W[14]);
    P(A, B, C, D, E, W[15]);
    P(E, A, B, C, D, R(16));
    P(D, E, A, B, C, R(17));
    P(C, D, E, A, B, R(18));
    P(B, C, D, E, A, R(19));

#undef K
#undef F

#define F(x, y, z) (x ^ y ^ z)
#define K 0x6ED9EBA1

    P(A, B, C, D, E, R(20));
    P(E, A, B, C, D, R(21));
    P(D, E, A, B, C, R(22));
    P(C, D, E, A, B, R(23));
    P(B, C, D, E, A, R(24));
    P(A, B, C, D, E, R(25));
    P(E, A, B, C, D, R(26));
    P(D, E, A, B, C, R(27));
    P(C, D, E, A, B, R(28));
    P(B, C, D, E, A, R(29));
    P(A, B, C, D, E, R(30));
    P(E, A, B, C, D, R(31));
    P(D, E, A, B, C, R(32));
    P(C, D, E, A, B, R(33));
    P(B, C, D, E, A, R(34));
    P(A, B, C, D, E, R(35));
    P(E, A, B, C, D, R(36));
    P(D, E, A, B, C, R(37));
    P(C, D, E, A, B, R(38));
    P(B, C, D, E, A, R(39));

#undef K
#undef F

#define F(x, y, z) ((x & y) | (z & (x | y)))
#define K 0x8F1BBCDC

    P(A, B, C, D, E, R(40));
    P(E, A, B, C, D, R(41));
    P(D, E, A, B, C, R(42));
    P(C, D, E, A, B, R(43));
    P(B, C, D, E, A, R(44));
    P(A, B, C, D, E, R(45));
    P(E, A, B, C, D, R(46));
    P(D, E, A, B, C, R(47));
    P(C, D, E, A, B, R(48));
    P(B, C, D, E, A, R(49));
    P(A, B, C, D, E, R(50));
    P(E, A, B, C, D, R(51));
    P(D, E, A, B, C, R(52));
    P(C, D, E, A, B, R(53));
    P(B, C, D, E, A, R(54));
    P(A, B, C, D, E, R(55));
    P(E, A, B, C, D, R(56));
    P(D, E, A, B, C, R(57));
    P(C, D, E, A, B, R(58));
    P(B, C, D, E, A, R(59));

#undef K
#undef F

#define F(x, y, z) (x ^ y ^ z)
#define K 0xCA62C1D6

    P(A, B, C, D, E, R(60));
    P(E, A, B, C, D, R(61));
    P(D, E, A, B, C, R(62));
    P(C, D, E, A, B, R(63));
    P(B, C, D, E, A, R(64));
    P(A, B, C, D, E, R(65));
    P(E, A, B, C, D, R(66));
    P(D, E, A, B, C, R(67));
    P(C, D, E, A, B, R(68));
    P(B, C, D, E, A, R(69));
    P(A, B, C, D, E, R(70));
    P(E, A, B, C, D, R(71));
    P(D, E, A, B, C, R(72));
    P(C, D, E, A, B, R(73));
    P(B, C, D, E, A, R(74));
    P(A, B, C, D, E, R(75));
    P(E, A, B, C, D, R(76));
    P(D, E, A, B, C, R(77));
    P(C, D, E, A, B, R(78));
    P(B, C, D, E, A, R(79));

#undef K
#undef F

    ctx->state[0] += A;
    ctx->state[1] += B;
    ctx->state[2] += C;
    ctx->state[3] += D;
    ctx->state[4] += E;
}


/*
 * Splits input message into blocks and processes them one by one. Also
 * checks how many 0 need to be padded and processes the last, padded, block.
 */
__device__ void sha1_gpu_update(sha1_gpu_context *ctx, unsigned char *input, int ilen) {
    int fill;
    unsigned long left;

    if (ilen <= 0)
        return;

    left = ctx->total[0] & 0x3F;
    fill = 64 - left;

    ctx->total[0] += ilen;
    ctx->total[0] &= 0xFFFFFFFF;

    if (ctx->total[0] < (unsigned long) ilen)
        ctx->total[1]++;

    if (left && ilen >= fill) {
        memcpy((void *) (ctx->buffer + left), (void *) input, fill);
        sha1_gpu_process(ctx, ctx->buffer);
        input += fill;
        ilen -= fill;
        left = 0;
    }

    while (ilen >= 64) {
        sha1_gpu_process(ctx, input);
        input += 64;
        ilen -= 64;
    }

    if (ilen > 0) {
        memcpy((void *) (ctx->buffer + left), (void *) input, ilen);
    }
}


/*
 * Process padded block and return hash to user.
 */
__device__ void sha1_gpu_finish(sha1_gpu_context *ctx, unsigned char *output) {
    unsigned long last, padn;
    unsigned long high, low;
    unsigned char msglen[8];


    high = (ctx->total[0] >> 29) | (ctx->total[1] << 3);
    low = (ctx->total[0] << 3);

    PUT_UINT32_BE(high, msglen, 0);
    PUT_UINT32_BE(low, msglen, 4);

    last = ctx->total[0] & 0x3F;
    padn = (last < 56) ? (56 - last) : (120 - last);

    sha1_gpu_update(ctx, (unsigned char *) sha1_padding, padn);
    sha1_gpu_update(ctx, msglen, 8);

    PUT_UINT32_BE(ctx->state[0], output, 0);
    PUT_UINT32_BE(ctx->state[1], output, 4);
    PUT_UINT32_BE(ctx->state[2], output, 8);
    PUT_UINT32_BE(ctx->state[3], output, 12);
    PUT_UINT32_BE(ctx->state[4], output, 16);
}

__constant__  unsigned char const_string[1024];
__constant__  unsigned char const_suffix[16];

//struct Lock {
//    int *mutex;
//
//    Lock(void) {
//        int state = 0;
//        cudaMalloc((void **) &mutex, sizeof(int));
//        cudaMemcpy(mutex, &state, sizeof(int), cudaMemcpyHostToDevice);
//    }
//
//    ~Lock(void) {
//        cudaFree(mutex);
//    }
//
//    __device__ void lock(void) {
//        while (atomicCAS(mutex, 0, 1) != 0);
//    }
//
//    __device__ void unlock(void) {
//        atomicExch(mutex, 1);
//    }
//};

//__device__ void lock(int *mutex) {
//    while (atomicCAS(mutex, 0, 1) != 0);
//}
//
//__device__ void unlock(int *mutex) {
//    atomicExch(mutex, 0);
//}

//__device__ void lock(int *mutex) {
//    while (atomicCAS(mutex, 0, 1) != 0);
//}
//
//__device__ void unlock(int *mutex) {
//    atomicExch(mutex, 0);
//}

__device__ int device_strlen(const char *s) {

    unsigned int count = 0;
    while (*s != '\0') {
        count++;
        s++;
    }
    return count;
}

__global__ void generate_nonce(unsigned char *nonce, unsigned char *sha, int *dev_mtx, int *dev_stop, int length) {

    int index = threadIdx.x + blockIdx.x * blockDim.x;

    while (!*dev_stop) {

        unsigned char string_aux[1024];
        memcpy(string_aux, const_string, length);

        unsigned char buf[21];
        buf[0] = index % 128;
        buf[1] = (index >> 8) % 128;
        buf[2] = (index >> 16) % 128;
        buf[3] = (index >> 24) % 128;
        buf[4] = '\0';
        memcpy(string_aux + length, buf, device_strlen((const char *) buf));

        unsigned char calculated_sha[21];
        sha1_gpu_context ctx;
        sha1_gpu_starts(&ctx);
        sha1_gpu_update(&ctx, (unsigned char *) string_aux, length + device_strlen((const char *) buf));
        sha1_gpu_finish(&ctx, calculated_sha);
        calculated_sha[20] = '\0';

        if (calculated_sha[15] == const_suffix[0] && calculated_sha[16] == const_suffix[1] &&
            calculated_sha[17] == const_suffix[2] && calculated_sha[18] == const_suffix[3] &&
            calculated_sha[19] == const_suffix[4]) {
            if (atomicCAS(dev_mtx, 0, 1) == 0) {
                *dev_stop = 1;
                memcpy(nonce, buf, device_strlen((const char *) buf) + 1);
                memcpy(sha, calculated_sha, 21);
                atomicExch(dev_mtx, 0);
            }
        }
//        memset(&ctx, 0, sizeof(sha1_gpu_context));
        index += blockDim.x * gridDim.x;
    }
}

int main() {

    unsigned char *str = (unsigned char *) "nonce project g";
    unsigned char *suffix = (unsigned char *) "\x28\x23\x46\x8D\xB0";

    printf("Given string: %s\n", str);
    printf("Given suffix: ");

    for (int i = 0; i < strlen((const char *) suffix); i++) {
        printf("%02X", suffix[i]);
    }
    printf("\n");

    unsigned char nonce[21];
    unsigned char sha[41];

    cudaMemcpyToSymbol(const_string, str, strlen((const char *) str));
    cudaMemcpyToSymbol(const_suffix, suffix, strlen((const char *) suffix));

    int *dev_stop;
    int *dev_mtx;
    unsigned char *dev_nonce;
    unsigned char *dev_sha;
    cudaError_t cudaStatus;

    cudaStatus = cudaSetDevice(0);
    if (cudaStatus != cudaSuccess) {
        fprintf(stderr, "cudaSetDevice failed!");
        goto Error;
    }

    cudaMallocManaged(&dev_stop, sizeof(int));
    *dev_stop = 0;

    cudaMalloc(&dev_mtx, sizeof(int));
    cudaMemset(dev_mtx, 0, sizeof(int));

    cudaStatus = cudaMalloc((void **) &dev_nonce, 21);
    if (cudaStatus != cudaSuccess) {
        fprintf(stderr, "cudaMalloc failed!");
        goto Error;
    }

    cudaStatus = cudaMalloc((void **) &dev_sha, 41);
    if (cudaStatus != cudaSuccess) {
        fprintf(stderr, "cudaMalloc failed!");
        goto Error;
    }

    generate_nonce<<<BLOCKS, THREADS>>>(dev_nonce, dev_sha, dev_mtx, dev_stop, strlen((const char *) str));
    // Errors when lunching the kernel
    cudaStatus = cudaGetLastError();
    if (cudaStatus != cudaSuccess) {
        fprintf(stderr, "Failed to generate nonce: %s\n", cudaGetErrorString(cudaStatus));
        goto Error;
    }

    // cudaDeviceSynchronize waits for the kernel to finish and returns
    // any errors encountered during the launch.
    cudaStatus = cudaDeviceSynchronize();
    if (cudaStatus != cudaSuccess) {
        fprintf(stderr, "cudaDeviceSynchronize returned error code %d after launching generate_nonce!\n", cudaStatus);
        goto Error;
    }

    cudaStatus = cudaMemcpy(nonce, dev_nonce, 21, cudaMemcpyDeviceToHost);
    if (cudaStatus != cudaSuccess) {
        fprintf(stderr, "Copy nonce from device to host failed!");
        goto Error;
    }

    cudaStatus = cudaMemcpy(sha, dev_sha, 41, cudaMemcpyDeviceToHost);
    if (cudaStatus != cudaSuccess) {
        fprintf(stderr, "Copy computed sha from device to host failed!");
        goto Error;
    }

    printf("Calculated nonce: %s\n", nonce);
    printf("Calculated nonce bytes: ");
    for (int i = 0; i < strlen((const char *) nonce); i++) {
        printf("%02X", nonce[i]);
    }
    printf("\n");

    printf("Calculated sha: ");
    for (int i = 0; i < strlen((const char *) sha); i++) {
        printf("%02X", sha[i]);
    }
    printf("\n");

    //cudaDeviceReset must be  called before exiting in order for profiling and
    // tracing tools such as Nsight and Visual Profiler to show complete traces.
    cudaStatus = cudaDeviceReset();
    if (cudaStatus != cudaSuccess) {
        fprintf(stderr, "cudaDeviceReset failed!");
        cudaFree(dev_stop);
        cudaFree(dev_nonce);
        cudaFree(dev_sha);
        return 1;
    }
    Error:
    cudaFree(dev_stop);
    cudaFree(dev_nonce);
    cudaFree(dev_sha);
    return 0;
}
