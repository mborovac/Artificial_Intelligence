package cetvrtiLabos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Genetic algorithm (written in Croatian for some reason) using 2 external files "test.txt" and "train.txt" 
 * to train and test the neural network.
 * 
 * @author Marko Borovac, march 2013
 *
 */
public class GenetskiAlgoritam {
	
	public static final Integer VELICINA_POPULACIJE = 50;
	public static final Integer K = 3;
	public static ArrayList<NeuronskaMreza> populacija = new ArrayList<NeuronskaMreza>();
	public static ArrayList<NeuronskaMreza> pomPopulacija = new ArrayList<NeuronskaMreza>();
	public static Integer generacija;
	public static HashMap<Double, Double> ulaz = new HashMap<Double, Double>();
	public static HashMap<Double, Double> test = new HashMap<Double, Double>();
	public static Double ukupnaPogreska;
	public static Double najvecaPogreska;
	public static Double najmanjaPogreska;
	static NeuronskaMreza najboljaJedinka;
	
	public static void main(String[] args) throws IOException {
		
		Double dopustenaPogreska = Double.parseDouble(args[0]);
		Integer brojIteracija = Integer.parseInt(args[1]);
		
		BufferedReader br = new BufferedReader(new FileReader("train.txt"));
	    try {
	        String line = br.readLine();
	        while (line != null) {
	            String[] pom = line.split("	");
	            ulaz.put(Double.parseDouble(pom[0]), Double.parseDouble(pom[1])); 
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
	    
	    br = new BufferedReader(new FileReader("test.txt"));
	    try {
	        String line = br.readLine();
	        while (line != null) {
	            String[] pom = line.split("	");
	            test.put(Double.parseDouble(pom[0]), Double.parseDouble(pom[1])); 
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
	    
		generacija = 1;
		for(Integer i = 0; i < VELICINA_POPULACIJE; i++) {
			NeuronskaMreza jedinka = new NeuronskaMreza();
			populacija.add(jedinka);
		}
		evaluacijaPopulacije();
		System.out.println("Generacija: "+generacija);
		ukupnaPogreska = 0.0;
		for(NeuronskaMreza jedinka : populacija) {
			ukupnaPogreska += jedinka.pogreska;
		}
		System.out.println("Najmanja pogreška je: "+najmanjaPogreska);
		
		while(generacija < brojIteracija && Math.abs(najmanjaPogreska) > dopustenaPogreska) {
			pomPopulacija.add(najboljaJedinka);
			while(pomPopulacija.size() < VELICINA_POPULACIJE) {
				NeuronskaMreza roditelj1 = null;
				NeuronskaMreza roditelj2 = null;
				NeuronskaMreza dijete = null;
				// postavljanje vjerojatnosti da ce jedinka biti roditelj
				odrediVjerojatnost(populacija);
				// odabir roditelja
				Random generator = new Random();
				Double vjerojatnost = generator.nextDouble();
				for(NeuronskaMreza jedinka : populacija) {
					if(vjerojatnost >= jedinka.pocetakRaspona && vjerojatnost < jedinka.krajRaspona) {
						roditelj1 = jedinka;
						break;
					}
				}
				vjerojatnost = generator.nextDouble();
				for(NeuronskaMreza jedinka : populacija) {
					if(vjerojatnost >= jedinka.pocetakRaspona && vjerojatnost < jedinka.krajRaspona) {
						roditelj2 = jedinka;
						break;
					}
				}
				if(roditelj1 != null && roditelj2 != null) {
					dijete = new NeuronskaMreza(roditelj1, roditelj2);
				} else {
					System.out.println("Nepostojeci roditelji!");
					System.exit(0);
				}
				// mutiranje djeteta
				if(dijete != null) {
					for(Double i : dijete.faktori) {
						vjerojatnost = generator.nextDouble();
						if(vjerojatnost < (4/13)/100) {
							double broj = generator.nextDouble() * K;
							i += broj;
						}
					}
					pomPopulacija.add(dijete);
				} else {
					System.out.println("Dijete nije stvoreno!");
					System.exit(0);
				}
			}
			populacija = pomPopulacija;
			generacija += 1;
			System.out.println("Generacija: "+generacija);
			pomPopulacija = new ArrayList<NeuronskaMreza>();
			evaluacijaPopulacije();
			ukupnaPogreska = 0.0;
			for(NeuronskaMreza jedinka : populacija) {
				ukupnaPogreska += jedinka.pogreska;
			}
			System.out.println("Najmanja pogreška je: "+najmanjaPogreska);
		}
		System.out.println("Ucenje gotovo! Testiram najbolju jedinku...");
		najboljaJedinka.Proracun(test);
		System.out.println("Pogreska najbolje jedinke je: "+najboljaJedinka.pogreska);
	}
	
	public static void odrediVjerojatnost(ArrayList<NeuronskaMreza> populacija) {
		Double ukupnaDobrota = 0.0;
		Double vjerojatnost = 0.0;
		Double pocetakRaspona = 0.0;
		for(NeuronskaMreza i : populacija) {
			ukupnaDobrota += i.dobrota;
		}
		for(NeuronskaMreza i : populacija) {
			vjerojatnost = i.dobrota/ukupnaDobrota;
			i.pocetakRaspona = pocetakRaspona;
			i.krajRaspona = i.pocetakRaspona + vjerojatnost;
			pocetakRaspona += vjerojatnost;
		}
	}
	
	public static void evaluacijaPopulacije() {
		// evaluacija populacije
		for(NeuronskaMreza jedinka : populacija) {
			jedinka.Proracun(ulaz);
		}
		// odredivanje najvece pogreske u populaciji
		najvecaPogreska = Math.abs(populacija.get(0).pogreska);
		najmanjaPogreska = Math.abs(populacija.get(0).pogreska);
		for(NeuronskaMreza jedinka : populacija) {
			if(jedinka.pogreska > najvecaPogreska) {
				najvecaPogreska = jedinka.pogreska;
			}
			if(Math.abs(jedinka.pogreska) < najmanjaPogreska) {
				najmanjaPogreska = Math.abs(jedinka.pogreska);
				najboljaJedinka = jedinka;
			}
		}
		// postavljanje dobrote populaciji
		for(NeuronskaMreza jedinka : populacija) {
			if(!(Math.abs(najvecaPogreska - Math.abs(jedinka.pogreska)) == 0)) {
				jedinka.dobrota = 1.0 - Math.abs(najvecaPogreska - Math.abs(jedinka.pogreska));
			} else {
				jedinka.dobrota = 0.0;
			}
		}
	}
}
