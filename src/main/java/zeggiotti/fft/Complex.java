package zeggiotti.fft;

public class Complex {

    public double real;
    public double imaginary;

    public Complex(double real, double imag) {
        this.real = real;
        this.imaginary = imag;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Complex)
            return real==((Complex)obj).real && imaginary==((Complex)obj).imaginary;
        else return false;
    }

}
