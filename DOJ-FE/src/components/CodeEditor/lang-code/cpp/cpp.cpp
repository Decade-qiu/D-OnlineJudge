// template
#include <iostream>
#include <vector>

using namespace std;

int main() {
    // 计算需要多少个 double 才能占用 256MB 的内存
    const size_t memory_target_mb = 256;
    const size_t double_size_in_bytes = sizeof(double);  // 每个 double 占 8 字节
    const size_t num_elements = (memory_target_mb * 1024 * 1024) / double_size_in_bytes;  // 需要多少个 double 才能达到 256MB

    // 创建一个大数组，分配 256MB 的内存
    vector<double> large_array(num_elements, 0.0);
    cout << "Allocated " << (large_array.size() * double_size_in_bytes) / (1024 * 1024) << " MB of memory." << endl;
    cout << "Hello, World!" << endl;
    return 0;
}