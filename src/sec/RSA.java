package sec;

import java.math.BigInteger;
import java.util.Random;

public class RSA {
	
    private BigInteger n;
    private BigInteger e;
    private BigInteger d;
    
    /*
     * RSA enkripcija radi tako sto uzmemo dva neka velika broja P i Q. Ova dva broja pomnozimo
     * i dobijemo N (public key). Onda nalazimo fi(n) = (p - 1)(q - 1) - predstavlja sve
     * coprime brojeve naseg N. Onda trazimo E tako sto nalazimo coprime od N takav da pripada
     * intervalu 1 < E < fi(n). Kada nadjemo E, trazimo D takav da je D * E(mod fi(n)) = 1
     */
    
    //Init setup
    public RSA() {
    	
    	//generisanje random key-eva
        Random random = new Random();
        BigInteger p = new BigInteger(512, 30, random);
        BigInteger q = new BigInteger(512, 30, random);
        
        //Nadjemo fi(n)
        BigInteger lambda = lcm(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
        
        //Nadjemo n tako sto pomnozimo nase brojeve
        this.n = p.multiply(q);
        
        //nadjemo D kao coprime lambde
        this.d = comprime(lambda);
        
        //E nam je inverz d i lambde
        this.e = d.modInverse(lambda);
    }
    
    //za dekripciju prosledimo argumente za desifrovanje
    public RSA(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }
    
    /*
     * Funkcija za nalazenje koprima za D
     * @param input - broj za koji trazimo 
     */
    private static BigInteger comprime(BigInteger input) {
    	
    	//krenemo od dva
        BigInteger candidate = BigInteger.valueOf(2);
        while (true) {
        	
        	//kada nadjemo odgovarajuci vratimo ga
            if (input.gcd(candidate).equals(BigInteger.ONE)) {
                return candidate;
            }
            candidate = candidate.add(BigInteger.ONE);
        }
    }

    /*
     * Funkcija za nalazenje least common multiple (LCM)
     */
    private BigInteger lcm(BigInteger input1, BigInteger input2) {
        return input1.multiply(input2).divide(input1.gcd(input2));
    }

    /*
     * Funkcija za desifrovanje poruka preko integera u bajtove
     */
    private BigInteger decrypt(BigInteger encryptedMessage) {
        return encryptedMessage.modPow(d, n);
    }

    /*
     * Funkcija za desifrovanje poruka preko bajtova u string
     */
    public String decryptString(String encryptedMessage) {
        return new String(decrypt(new BigInteger(encryptedMessage)).toByteArray());
    }

    /*
     * Funkcija za enkripciju
     */
    private BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n);
    }

    /*
     * Funkcija za enkripciju preko bajtova stringa
     */
    public String encryptString(String message) {
        byte[] data = message.getBytes();
        return encrypt(new BigInteger(data)).toString();
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getE() {
        return e;
    }
}
