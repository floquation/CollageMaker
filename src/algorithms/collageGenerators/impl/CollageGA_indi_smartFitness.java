package algorithms.collageGenerators.impl;

import java.awt.Color;

import algorithms.collageGenerators.CollageAlgorithmUtils;
import algorithms.collageGenerators.template.CollageAlgorithm;
import algorithms.collageGenerators.template.GeneticAlgorithm;
import editor.data.ImageRefList;
import editor.data.ResultXML;

public class CollageGA_indi_smartFitness extends CollageGA_indi {

	public CollageGA_indi_smartFitness(int chromLength, int populationSize, float pCrossover, float lCrossover,
			float pMutate, float pMutateRepeat, int nElitism, double fitnessGoal, float pExtinction,
			boolean applyRelativeExtinction, double extinctErrorIniThreshold, double extinctErrorThreshold,
			double extinctErrorRelThreshold, int nIniExtinctionWipes,
			ImageRefList refList, ResultXML result) {
		
		super(chromLength, populationSize, pCrossover, lCrossover,
				pMutate, pMutateRepeat, nElitism, fitnessGoal, pExtinction,
				applyRelativeExtinction, extinctErrorIniThreshold, extinctErrorThreshold,
				extinctErrorRelThreshold, nIniExtinctionWipes,
				refList, result);
	}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ GENETIC ALGORITHM-SPECIFIC METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	/**
	 * Return the fitness as 1/errorSq,
	 * 
	 * where errorSq = scale*sum((error_blue^2+error_green^2+error_red^2)*duplicityError),
	 * with scale a numerical constant which forces the typical fitness near 1000,
	 * and duplicityError is the degree of local unicity.
	 * 
	 * @author Kevin van As
	 */
	@Override
	protected double fitness(Chromosome chrom) {
		double errorSq = 0;
		
		//Compare the color for each gridElement
		for(int i = 0; i<getChromLength(); i++){
			int x = i%colAlg.result.grid.size.w;
			int y = (i-x)/colAlg.result.grid.size.w;
		
			Color colorFit = colAlg.refList.getImage(colAlg.keyList.get(chrom.data[i])).getColor(); //TODO: null-pointer if the refList is changed concurrently such that the requested ID no longer exists.
			Color colorTarget = colAlg.result.grid.elements[x][y].color;
			
			errorSq += getDuplicity(chrom, x, y)*CollageAlgorithmUtils.errorSq(colorFit,colorTarget,colAlg.result);
		}
		
//		System.out.println("(CollageGA) Fitness = " + 1/errorSq);
		
		//Fitness is propto 1/error (ignore the sqrt).
		if(errorSq == 0)
			return Double.MAX_VALUE;
		return 1/errorSq;
	}
	
	/**
	 * Counts how frequently the image contained within {@code chrom.data[cell]} is used in the neighborhood of {@code cell}.
	 * 
	 * The neighborhood is defined by the stencil. //TODO: variable stencil size
	 * 
	 * @author Kevin van As
	 * @param cell
	 * @return
	 */
	private int getDuplicity(Chromosome chrom, int x, int y){
		int count = 0; //becomes 1 automatically when comparing with self
		
		int stencilOffset = 2; //TODO: Modifiable square
		int cell = x+y*colAlg.result.grid.size.w;
		int xnb_ini = Math.max(0, x-stencilOffset);
		int xnb_end = Math.min(colAlg.result.grid.size.w, x+stencilOffset);
		int ynb_ini = Math.max(0, y-stencilOffset);
		int ynb_end = Math.min(colAlg.result.grid.size.h, y+stencilOffset);
		for(int ynb = ynb_ini; ynb < ynb_end; ynb++){
			for(int xnb = xnb_ini; xnb < xnb_end; xnb++){
				int cell_nb = xnb+ynb*colAlg.result.grid.size.w;
				count += chrom.data[cell_nb] == chrom.data[cell] ? 1 : 0;
			}
		}
				
		return count;
	}
	
	//TODO: Test the getDuplicity() method
	//TODO: smartMutate should use the getDuplicity() method as well -> Override mutate()?
	
}
