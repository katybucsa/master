//
// Created by Katy on 08-Dec-21.
//

#include <stdio.h>
#include <vector>
#include <cuda_runtime.h>

using namespace std;

__device__ void lock(int *mutex) {
    while (atomicCAS(mutex, 0, 1) != 0);
}

__device__ void unlock(int *mutex) {
    atomicExch(mutex, 0);
}

//__global__ void ck_test_lock(int *, int*);
//__global__ void ck_test_lock_2(int *, int*);
__global__ void ck_test_lock_3(int *, int *);
//__global__ void ck_test_lock_4(int *, int *);

int *mutex;

int test_cuda_mutex() {
    cudaMalloc(&mutex, sizeof(int));
    cudaMemset(mutex, 0, sizeof(int));
    int *status;
    cudaMallocManaged(&status, sizeof(int));
    *status = 0;

    // This one can pass since it contains 2 blocks of threads
    // ck_test_lock<<<2, 1>>>(status, mutex);
    // cudaDeviceSynchronize();

    // These two will not pass; deadlock between threads in a warp
    // ck_test_lock<<<1, 2>>>(status, mutex);
    // ck_test_lock<<<1, 32>>>(status, mutex);
    // cudaDeviceSynchronize();

    // This one can pass since it contains 2 blocks of threads
    // ck_test_lock_2<<<2, 1>>>(status, mutex);
    // ck_test_lock_2<<<1, 2>>>(status, mutex);
    // cudaDeviceSynchronize();

    // This works well
    ck_test_lock_3<<<64, 1024>>>(status, mutex);
    // This works badly
    // ck_test_lock_4<<<1, 32>>>(status, mutex);

    if (cudaSuccess != cudaGetLastError())
        printf("Error!\n");
    cudaDeviceSynchronize();
    printf("result is %d\n\n\n", *status);
    cudaFree(status);
    return 0;
}

//__global__ void ck_test_lock_4(int *status, int *mutex) {
//    bool leave = true;
//    while (leave) {
//        if (atomicCAS(mutex, 0, 1) == 0) {
//            status[0] = threadIdx.x;
//            leave = false;
//            atomicExch(mutex, 0);
//            break;
//        }
//    }
//}

__global__ void ck_test_lock_3(int *status, int *mutex) {
    bool leave = true;
    while (!*status) {
        if (atomicCAS(mutex, 0, 1) == 0) {
            *status = 1;
//            leave = false;
            atomicExch(mutex, 0);
        }
//        break;
    }
}

//__global__ void ck_test_lock_2(int *status, int *mutex) {
//    int c = 0;
//    LOCKFRONT:
//    if (c > 10) {
//        // thread 1 runs 10 times; go out
//        // thread 0 will start to work.
//        goto EXIT;
//    }
//    printf("blkID = %d, trdID = %d; mutex = %d\n", blockIdx.x, threadIdx.x, *mutex);
//    if (atomicCAS(mutex, 0, 1) == 0) {
//        // thread 0 is blocked here
//        printf("%d: I start the first line  =====================\n", threadIdx.x);
//        status[0] = threadIdx.x;
//        atomicExch(mutex, 0);
//    } else {
//        // thread 1 goes here
//        c++;
//        printf("%d %d %d\n", threadIdx.x, c, *mutex);
//        goto LOCKFRONT;
//    }
//    EXIT:
//}
//
//__global__ void ck_test_lock(int *status, int *mutex) {
//    printf("blkID = %d, trdID = %d; mutex = %d\n", blockIdx.x, threadIdx.x, *mutex);
//    lock(mutex);
//    printf("============mutex is %d ========\n", *mutex);
//    status[0] = threadIdx.x;
//    unlock(mutex);
//}

int main() {
    for (int T = 0; T < 1; T++) {
        test_cuda_mutex();
    }
}