const int N = 8;

extern "C" {
	bool solve(int depth);
}

int ans[N];
bool v[N];

int abs(int x) {
	return x > 0 ? x : -x;
}

bool solve(int depth) {
	if (depth >= N) return true;
	for (int i = 0; i < N; ++i) {
		if (v[i]) continue;
		bool flag = true;
		for (int j = 0; j < depth; ++j) 
			if (abs(ans[j] - i) == abs(depth - j)) flag = false;
		if (!flag) continue;

		v[i] = true;
		ans[depth] = i;

		if (solve(depth + 1)) return true;

		v[i] = false;
	}
	return false;
}

