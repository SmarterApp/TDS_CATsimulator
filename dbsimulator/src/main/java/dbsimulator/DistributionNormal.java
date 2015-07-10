/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package dbsimulator;

public class DistributionNormal {

  private double _mean = 0.0;
  private double _stdDev = 1.0;
  private boolean _isSet = false;

  static final double[] _a = { -3.969683028665376e+01, 2.209460984245205e+02,
    -2.759285104469687e+02, 1.383577518672690e+02,
    -3.066479806614716e+01, 2.506628277459239e+00 };

  static final double[] _b = { -5.447609879822406e+01, 1.615858368580409e+02,
    -1.556989798598866e+02, 6.680131188771972e+01,
    -1.328068155288572e+01 };

  static final double[] _c = { -7.784894002430293e-03,
    -3.223964580411365e-01, -2.400758277161838e+00,
    -2.549732539343734e+00, 4.374664141464968e+00,
    2.938163982698783e+00 };

  static final double[] _d = { 7.784695709041462e-03, 3.224671290700398e-01,
    2.445134137142996e+00, 3.754408661907416e+00 };

  public DistributionNormal() {
    _mean = 0.0;
    _stdDev = 1.0;
    _isSet = false;
  }

  public DistributionNormal(double mean, double stDev) {
    _mean = mean;
    _stdDev = stDev;
    _isSet = true;
  }

  public void SetParameters(double mean, double stdev) {
    _mean = mean;
    _stdDev = stdev;
    _isSet = true;
  }

  public double InvCDF(double p) {
    if (!_isSet)
      throw (new DBSimulatorException(
          "Normal InvCDF called without first setting parameters"));
    return _mean + _stdDev * _InvCDF(p);
  }

  /*
   * Refer: http://home.online.no/~pjacklam/notes/invnorm/
   */
  private double _InvCDF(double p) {

    double q, t, u;

    if (Double.isNaN(p) || p > 1.0 || p < 0.0)
      return Double.MAX_VALUE;

    if (p == 0.0)
      return Double.MIN_VALUE;

    if (p == 1.0)
      return Double.MAX_VALUE;

    q = Math.min(p, 1 - p);

    if (q > 0.02425) {
      /* Rational approximation for central region. */
      u = q - 0.5;
      t = u * u;
      u = u
          * (((((_a[0] * t + _a[1]) * t + _a[2]) * t + _a[3]) * t + _a[4])
              * t + _a[5])
              / (((((_b[0] * t + _b[1]) * t + _b[2]) * t + _b[3]) * t + _b[4])
                  * t + 1);
    } else {
      /* Rational approximation for tail region. */
      t = Math.sqrt(-2 * Math.log(q));
      u = (((((_c[0] * t + _c[1]) * t + _c[2]) * t + _c[3]) * t + _c[4])
          * t + _c[5])
          / ((((_d[0] * t + _d[1]) * t + _d[2]) * t + _d[3]) * t + 1);
    }

    return (p > 0.5 ? -u : u);
  }
}
