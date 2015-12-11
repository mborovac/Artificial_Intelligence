package cetvrtiLabos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NeuronskaMreza {
	public ArrayList<Double> faktori;
	public Double pogreska = 0.0;
	public Double dobrota;
	public Double vjerojatnostOdabira;
	public Double pocetakRaspona;
	public Double krajRaspona;
	
	public NeuronskaMreza() {
		Random generator = new Random();
		faktori = new ArrayList<Double>();
		for (Integer i = 0; i < 13; i++) {
			Double faktor = generator.nextDouble() * 20.0 - 10.0;
			this.faktori.add(faktor);
		}
	}
	
	public NeuronskaMreza(NeuronskaMreza roditelj1, NeuronskaMreza roditelj2) {
		faktori = new ArrayList<Double>();
		for(Integer i = 0; i < 13; i++) {
			Double faktor = (roditelj1.faktori.get(i) + roditelj2.faktori.get(i)) / 2;
			this.faktori.add(faktor);
		}
	}
	
	//faktori: 0.-3. su w0-w3, 4.-7. su sigma0-sigma3, 8.-11. su w4-w7 i 12. je sigma4
	public void Proracun(HashMap<Double, Double> ulaz) {
		pogreska = 0.0;
		for(Double x : ulaz.keySet()) {
			Double y = ulaz.get(x);
			Double izlazPrvi;
			Double izlazDrugi;
			Double izlazTreci;
			Double izlazCetvrti;
			Double izlaz;
			Double rezultat = (x*faktori.get(0)-faktori.get(4)); // x * w0 - sigma0
			izlazPrvi = 1 / (1+Math.pow(Math.E,-rezultat)); // izlazna funkcija prvog neurona 2. razine
			rezultat = (x*faktori.get(1)-faktori.get(5));
			izlazDrugi = 1 / (1+Math.pow(Math.E,-rezultat));
			rezultat = (x*faktori.get(2)-faktori.get(6));
			izlazTreci = 1 / (1+Math.pow(Math.E,-rezultat));
			rezultat = (x*faktori.get(3)-faktori.get(7));
			izlazCetvrti = 1 / (1+Math.pow(Math.E,-rezultat));
			izlaz = izlazPrvi * faktori.get(8) + izlazDrugi * faktori.get(9) + izlazTreci * faktori.get(10) + 
					izlazCetvrti * faktori.get(11) - faktori.get(12);
			Double error = Math.pow(y - izlaz, 2);
			this.pogreska += error;
		}
		this.pogreska = this.pogreska/13;
	}
}
