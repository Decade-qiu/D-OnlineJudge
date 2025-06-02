// template
#include <iostream>
#include <vector>

using namespace std;

int main() {
    int a, b, n;
    long long x = 0;
    vector<int> nums(4 * 64 * 1024 * 1024);
    for (int i = 0;i < 1e9;i++) x += i;
    cout << "sum: " << x << endl;
    return 0;
}