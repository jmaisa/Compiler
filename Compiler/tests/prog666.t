{
int i; int j; float v; float x; float[100] a;
		do i = i+1; while( a[i] < v);
		do j = j-1; while( a[j] > v);
		if i >= j then break;
		x = a[i]; a[i] = a[j]; a[j] = x;
}





