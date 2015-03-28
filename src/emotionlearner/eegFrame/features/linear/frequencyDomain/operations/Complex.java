/*
 * Created on 2006.05.20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package emotionlearner.eegFrame.features.linear.frequencyDomain.operations;

/**
 * @author Princeton
 *
 * @version Razred koji implentira kompleksne brojeve i njihovu aritmetiku. Uzeto sa web
 * stranice: http://www.cs.princeton.edu/introcs/97data/Complex.java.html
 * 
 */
public class Complex {
	private final double re;   // the real part
    private final double im;   // the imaginary part

    // create a new object with the given real and imaginary parts
    public Complex(double real, double imag) {
        this.re = real;
        this.im = imag;
    }

    // return a string representation of the invoking object
    public String toString()  { return re + " + " + im + "i"; }

    // return |this|
    public double abs() { return Math.sqrt(re*re + im*im);  }
    // return phase angle
    public double angle(){ return Math.atan(im/re);}
    // return a new object whose value is (this + b)
    public Complex plus(Complex b) { 
        Complex a = this;             // invoking object
        double real = a.re + b.re;
        double imag = a.im + b.im;
        Complex sum = new Complex(real, imag);
        return sum;
    }

    // return a new object whose value is (this - b)
    public Complex minus(Complex b) { 
        Complex a = this;   
        double real = a.re - b.re;
        double imag = a.im - b.im;
        Complex diff = new Complex(real, imag);
        return diff;
    }

    // return a new object whose value is (this * b)
    public Complex times(Complex b) {
        Complex a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        Complex prod = new Complex(real, imag);
        return prod;
    }
    public Complex divide(Complex b){
    	Complex a = this;
    	Complex temp = a.times(b.conjugate());
    	double tempb = Math.pow(b.abs(),2.0);
    	return new Complex(temp.getReal()/tempb,temp.getImaginary()/tempb);
    }
    public Complex pow(int power){
    	Complex powers = this;
    	Complex temp = new Complex(powers.re, powers.im);
    	for (int i=power; i>1; i--){
    		temp = powers.times(temp);
    	}
    	return temp;
    }
    public double getReal(){
    	return this.re;
    }
    public double getImaginary(){
    	return this.im;
    }
    // return a new object whose value is (this * alpha)
    public Complex times(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }
    public Complex plus(double alpha) {
        return new Complex(re + alpha, im);
    }
    // return a new object whose value is the conjugate of this
    public Complex conjugate() {  return new Complex(re, -im); }
}
