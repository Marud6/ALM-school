#pragma once
#include <vector>
#include <cstddef>
#include <atomic>

extern std::atomic<bool> terminating;

template <typename T>
class MergeSort {
private:
    static void merge(std::vector<T>& arr, size_t left, size_t mid, size_t right) {
        std::vector<T> temp;
        size_t i = left, j = mid;

        while (i < mid && j < right) {
            if (arr[i] <= arr[j])
                temp.push_back(arr[i++]);
            else
                temp.push_back(arr[j++]);
        }

        while (i < mid) temp.push_back(arr[i++]);
        while (j < right) temp.push_back(arr[j++]);

        for (size_t k = 0; k < temp.size(); ++k)
            arr[left + k] = temp[k];
    }

    static void mergeSort(std::vector<T>& arr, size_t left, size_t right) {
        if (right - left <= 1) return;
        if (terminating.load(std::memory_order_relaxed))
            return;

        size_t mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid, right);
        merge(arr, left, mid, right);
    }

public:
    static void sort(std::vector<T>& arr) {
        mergeSort(arr, 0, arr.size());
        return;
    }
};
