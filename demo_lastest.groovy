// Using the lastest compiled mahout-core jar

import org.apache.mahout.math.Matrix
import org.apache.mahout.math.DenseMatrix

  def hilbert =  { int n ->
    Matrix r = new DenseMatrix(n, n)
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        r.set(i, j, 1.0 / (i + j + 1))
      }
    }
    return r
  }


Matrix m = hilbert(5)

// make sure it is the hilbert matrix we know and love
assert m.get(0, 0) == 1.0
assert m.get(0, 1) == 0.5
assert m.get(2, 3) == 1 / 6.0

println('All tests were passed successfully.')