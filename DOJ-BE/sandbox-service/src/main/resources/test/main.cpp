#include <iostream>
#include <vector>

using namespace std;

int main() {
    long long ans = 0;
    vector<int> v;
    for (int i = 0; i < 10000000; i++) {
        ans += i;
        v.push_back(i);
    }
    cout << ans << " test success!" << endl;
    return 0;
}