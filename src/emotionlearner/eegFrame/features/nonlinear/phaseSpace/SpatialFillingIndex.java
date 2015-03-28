/*
 * Created on 2006.04.28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package emotionlearner.eegFrame.features.nonlinear.phaseSpace;
/**
 * @author Jovic
 * 
 * @version Ovaj razred ima funkciju raèunanja indeksa prostorne popunjenosti za danu
 * matricu podataka. 
 * 
 */
public class SpatialFillingIndex {
	private double index;	//vrijednost indeksa prostorne popunjenosti
	private double sumS;	// vrijednost sume "s"
	private int finesse;	// korijen broja kvadrata pri podijeli faznog prostora na finesse*finesse kvadrata
	
	/**
	 * Konstruktor raèuna indeks prostorne popunjenosti, prema autorima Oliver Faust, Rajendra Acharya U, SM Krishnan and Lim Choo Min,
	 * objavljeno u: BioMedical Engineering OnLine 2004, dostupno na:  http://www.biomedical-engineering-online.com/content/3/1/30 . 
	 * @param finesse Korijen broja kvadrata koji dijele fazni prostor na finesse*finesse kvadrata.
	 * @param xMax Najveæi element matrice A aMatrix
	 * @param aMatrix Matrica A algoritma SFI.
	 */
	public SpatialFillingIndex(int finesse, double xMax, double[][] aMatrix){
		double[][] bMatrix;	// matrica B algoritma SFI
		double [][] cMatrix; // matrica C (ujedno i P i Q) algoritma SFI
		bMatrix = new double[aMatrix.length][aMatrix[0].length];
		this.finesse = finesse;
		
		// Raèunanje matrice B
		for (int i=0; i<aMatrix.length; i++){
			for (int j=0; j<aMatrix[i].length; j++){
				bMatrix[i][j] = aMatrix[i][j] / xMax; 
			}
		}
		cMatrix = new double[finesse][finesse];
		for (int i=0; i<finesse; i++){
			for (int j=0; j<finesse; j++){
				cMatrix[i][j]=0;
			}
		}
		// Raèunanje matrice C - broja toèaka koje padaju u (i,j). kvadrat.
		double xStart, yStart, xEnd, yEnd;
		boolean found = false;
		for (int i=0; i<finesse; i++){
			for (int j=0; j<finesse; j++){
				xStart = -1 + (double)(2*i)/finesse;
				xEnd = -1 + (double)(2*(i+1))/finesse;
				yStart = -1 + (double)(2*j)/finesse;
				yEnd = -1 + (double)(2*(j+1))/finesse;
				for (int k=0; k<bMatrix.length; k++){
					found = false;
					for (int l=0; l<bMatrix[k].length; l++){
						if (l==0) {
							if (bMatrix[k][l]>=xStart && bMatrix[k][l]<xEnd){
								found = true;
							}
						}
						else {
							if (found){
								if (bMatrix[k][l]>=yStart && bMatrix[k][l]<yEnd){
									cMatrix[i][j]++;
								}
							}
						}
					}
				}
			}
		}
		double sum = 0;
		
		for (int i=0; i<finesse; i++){
			for (int j=0; j<finesse; j++){
				sum += cMatrix[i][j];
			}
		}
		// Odreðivanje elemenata matrice P i Q odjednom.
		for (int i=0; i<finesse; i++){
			for (int j=0; j<finesse; j++){
				cMatrix[i][j] /= sum;	//pMatrix
				cMatrix[i][j] = Math.pow(cMatrix[i][j],2.0); //qMatrix
			}
		}
		sum = 0;
		// Raèunanje sume "s" iz matrice Q
		for (int i=0; i<finesse; i++){
			for (int j=0; j<finesse; j++){
				sum += cMatrix[i][j];
			}
		}
		this.sumS = sum;
		// Odreðivanje SFI-a
		this.index = this.sumS / Math.pow(finesse,2);
		this.index *= 1000;		// Da bi indeks bio > 1, inaèe je reda 10e-3.
	}
	/**
	 * 
	 * @return Indeks prostorne popunjenosti SFI
	 */
	public double getIndex(){
		return this.index;
	}
	/**
	 * 
	 * @return Sumu "s" algoritma SFI
	 */
	public double getSum(){
		return this.sumS;
	}
	/**
	 * 
	 * @return Korijen broja kvadrata koji dijele fazni prostor na finesse*finesse kvadrata.
	 */
	public int getFinesse(){
		return this.finesse;
	}
	public static final int MINIMAL_LENGTH_FOR_EXTRACTION = 5;
}
