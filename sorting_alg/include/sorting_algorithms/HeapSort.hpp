#pragma once
#include <vector>
#include <cstddef>
#include <utility>
#include <atomic>

extern std::atomic<bool> terminating;

template <typename T>
class HeapSort {
private:
    static void heapify(std::vector<T>& arr, size_t n, size_t i) {
        if (terminating.load(std::memory_order_relaxed))
            return;
        size_t largest = i;
        size_t left = 2 * i + 1;
        size_t right = 2 * i + 2;

        if (left < n && arr[left] > arr[largest])
            largest = left;

        if (right < n && arr[right] > arr[largest])
            largest = right;

        if (largest != i) {
            std::swap(arr[i], arr[largest]);
            heapify(arr, n, largest);
        }
    }

public:
    static void sort(std::vector<T>& arr) {
        size_t n = arr.size();

        for (int i = static_cast<int>(n) / 2 - 1; i >= 0; --i)
            heapify(arr, n, i);

        for (int i = static_cast<int>(n) - 1; i > 0; --i) {
            std::swap(arr[0], arr[i]);
            heapify(arr, i, 0);
        }
    }
};
