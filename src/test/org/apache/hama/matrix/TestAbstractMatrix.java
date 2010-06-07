package org.apache.hama.matrix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.hama.HamaCluster;
import org.apache.hama.HamaConfiguration;
import org.apache.hama.examples.MatrixNorm;
import org.apache.hama.matrix.Matrix.Norm;
import org.apache.log4j.Logger;

public class TestAbstractMatrix extends HamaCluster {
  static final Logger LOG = Logger.getLogger(TestAbstractMatrix.class);
  private int SIZE = 10;
  private Matrix m1;
  private Matrix m2;
  private HamaConfiguration conf;
  private double gap = 0.000001;
  
  /**
   * @throws UnsupportedEncodingException
   */
  public TestAbstractMatrix() throws UnsupportedEncodingException {
    super();
  }

  public void setUp() throws Exception {
    super.setUp();

    conf = getConf();
    m1 = DenseMatrix.random(conf, SIZE, SIZE);
    m2 = SparseMatrix.random(conf, SIZE, SIZE);
  }

  public void testTransposeAndNorm() throws IOException {
    normTest(m1);
    normTest(m2);
  }

  public void normTest(Matrix matrix) throws IOException {
    double norm1 = MatrixNorm.norm(matrix, Norm.One);
    double verify_norm1 = MatrixTestCommon.verifyNorm1(matrix);
    gap = norm1 - verify_norm1;
    assertTrue(gap < 0.000001 && gap > -0.000001);

    double normInfinity = MatrixNorm.norm(matrix, Norm.Infinity);
    double verify_normInf = MatrixTestCommon.verifyNormInfinity(matrix);
    gap = normInfinity - verify_normInf;
    assertTrue(gap < 0.000001 && gap > -0.000001);
    
    double normFrobenius = MatrixNorm.norm(matrix, Norm.Frobenius);
    double verify_normFrobenius = MatrixTestCommon.verifyNormFrobenius(matrix);
    gap = normFrobenius - verify_normFrobenius;
    assertTrue(gap < 0.000001 && gap > -0.000001);
    
    double normMaxValue = MatrixNorm.norm(matrix, Norm.Maxvalue);
    double verify_normMV = MatrixTestCommon.verifyNormMaxValue(matrix);
    gap = normMaxValue - verify_normMV;
    assertTrue(gap < 0.000001 && gap > -0.000001);
  }
}
