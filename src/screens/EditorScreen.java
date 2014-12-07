package screens;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import algorithms.Algorithm;
import algorithms.collageGenerators.CollageAlgorithmUtils;
import algorithms.collageGenerators.impl.BruteForce_Unique;
import algorithms.collageGenerators.impl.BruteForce_Unique2;
import algorithms.collageGenerators.impl.BruteForce_Unique3;
import algorithms.collageGenerators.impl.BruteForce_Unique4;
import algorithms.collageGenerators.impl.CheckAll;
import algorithms.collageGenerators.impl.CheckAllRO_Unique;
import algorithms.collageGenerators.impl.CollageGA;
import algorithms.collageGenerators.impl.CollageGA_Unique;
import algorithms.collageGenerators.impl.CollageGA_indi;
import algorithms.collageGenerators.impl.CollageGA_indi_smartFitness;
import editor.data.ImageRefList;
import editor.data.ImageTools;
import editor.data.ResultXML;
import editor.io.xmlReader;
import editor.io.xmlWriter;
import framework.Game;
import framework.Screen;

public class EditorScreen extends Screen {
	
	JPanel pnlMain;
	
	public EditorScreen(Game game) {
		super(game);
	}
						
	private static boolean runCheckAll = false;
	private static boolean runCheckAllROUnique = false;
	private static boolean runBruteForceUnique = false;
	private static boolean runBruteForceUnique2 = false;
	private static boolean runBruteForceUnique3 = false;
	private static boolean runBruteForceUnique4 = false;
	private static boolean runGA = false;
	private static boolean runGAIndi = false;
	private static boolean runGAIndiSmartFitness = true;
	private static boolean runGAUnique = false;
	private static boolean rerunAlgorithm =
			 runBruteForceUnique2 || runBruteForceUnique3 || runBruteForceUnique4 ||
			 runBruteForceUnique || runCheckAll || runGA || runGAIndi || runGAUnique ||
			 runCheckAllROUnique || runGAIndiSmartFitness ;
	
	@Override
	public void init(){
		
		//Test:
		ImageRefList testRefListXML = xmlReader.readXML_refList(game.getGlobalVars().workspace + "\\refList.xml");
		ResultXML testResultXML;
		
		if(!rerunAlgorithm){
			testResultXML = xmlReader.readXML_result(testRefListXML, game.getGlobalVars().workspace + "\\new_result.xml");
		}else{
			testResultXML = xmlReader.readXML_result(testRefListXML, game.getGlobalVars().workspace + "\\result.xml");
		}
		
		if(testResultXML == null)	testResultXML = new ResultXML(); 		//auto-default if corrupted file
		if(testRefListXML == null)	testRefListXML = new ImageRefList();	//auto-default if corrupted file
		
//		if(testRefListXML !=null) testRefListXML = xmlReader.readXML_refList(game.getGlobalVars().workspace + "\\new_refList.xml");
//		if(testResultXML !=null) testResultXML = xmlReader.readXML_result(testRefListXML, game.getGlobalVars().workspace + "\\new_result.xml");
//		
//		if(testRefListXML !=null) xmlWriter.writeXML_refList(game.getGlobalVars().workspace + "\\new_refList2.xml", testRefListXML);
//		if(testResultXML !=null) xmlWriter.writeXML_result(game.getGlobalVars().workspace + "\\new_result2.xml", testResultXML);
		
	//Test: importImage
//		boolean success = testRefListXML.importImage("C:\\Users\\Kevin van As\\Dropbox\\Java Projects\\CollageMaker\\Pictures\\Jester\\Equation SignatureLook2.jpg");
//		System.out.println("ImageRefListXML.importImage --> success = " + success);
		
	//Test: importImagesRecursively
//		testRefListXML.importImagesFromFileSystem("C:\\Users\\Kevin van As\\Dropbox\\Java Projects\\CollageMaker\\Pictures\\");
		
//		Color color = ImageTools.computeAvgColor(testRefListXML.getImage(1));
//		testRefListXML.getImage(0).color = color;
//		System.out.println(color.toString());
	
//	//Smart-clip:
//		Image toClipImg = testRefListXML.getImage(10);
//		toClipImg.smartClip(0.5, 1, Image.SMARTCLIP_CENTERED);
//		BufferedImage clippedImg = ImageTools.generateClippedImage(toClipImg);
//		try {			
//		    File outputfile = new File(game.getGlobalVars().workspace + "\\" + toClipImg.getName() + "_clipped.png");
//		    ImageIO.write(clippedImg, "png", outputfile);
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}
		
	//GUI:
//		pnlMain = new JPanel();
//		pnlMain.setLayout(new BorderLayout());
//		game.setWindowContainer(pnlMain);
//		game.setWindowSize(800, 300);
//		game.setWindowCentered(true);

		
	//Compute colors: (this method sets grid.elements to a properly-sized array)
		if(rerunAlgorithm) ImageTools.computeAvgColorGrid(testResultXML, testRefListXML.getImage(testResultXML.orgImageId));
		
	//Genetic Algorithm:
		if(runGA) runGA(testResultXML, testRefListXML);
		if(runGAIndi) runGAIndi(testResultXML, testRefListXML);
		if(runGAIndiSmartFitness) runGAIndiSmartFitness(testResultXML, testRefListXML);
		if(runGAUnique) runGAUnique(testResultXML, testRefListXML);
	//Check-All:
		if(runCheckAll) runCheckAll(testResultXML, testRefListXML);
		if(runCheckAllROUnique) runCheckAllROUnique(testResultXML, testRefListXML);
	//Brute-force Unique:
		if(runBruteForceUnique) runBruteForceUnique(testResultXML, testRefListXML);
	//Brute-force Unique:
		if(runBruteForceUnique2) runBruteForceUnique2(testResultXML, testRefListXML);
	//Brute-force Unique:
		if(runBruteForceUnique3) runBruteForceUnique3(testResultXML, testRefListXML);
	//Brute-force Unique:
		if(runBruteForceUnique4) runBruteForceUnique4(testResultXML, testRefListXML);
		
		if(rerunAlgorithm){
			System.out.println("UnicityMap = " + CollageAlgorithmUtils.getGlobalUnicityMap(testResultXML, testRefListXML).toString());
		}
		
	//Other output:
		BufferedImage colorMap = ImageTools.generateColorMapImage(testResultXML);
		try {
			System.out.println("Starting writing the colorMap to file.");
			
		    File outputfile = new File(game.getGlobalVars().workspace + "\\" + testResultXML.name + "_colorMap.png");
		    ImageIO.write(colorMap, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
	//"Save" files
		if(testRefListXML !=null) xmlWriter.writeXML_refList(game.getGlobalVars().workspace + "\\new_refList.xml", testRefListXML);
		if(testResultXML !=null) xmlWriter.writeXML_result(game.getGlobalVars().workspace + "\\new_result.xml", testResultXML);
		System.exit(0); //TODO: We will now exit immediately while we do not have a GUI anyway
	}

	private void runBruteForceUnique(ResultXML testResultXML, ImageRefList testRefListXML){
		BruteForce_Unique bf = new BruteForce_Unique(testResultXML, testRefListXML);
		bf.loop();
		
		double[][] errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		BufferedImage errorImg = ImageTools.generateErrorLinImage(testResultXML, errorSq);
		BufferedImage logErrorImg = ImageTools.generateErrorLogImage(testResultXML, errorSq);
		BufferedImage resultImg = ImageTools.generateResultImage(testRefListXML, testResultXML);
		try {
			System.out.println("(BFU) Starting writing images to file.");
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU_" + testResultXML.name + ".png");
		    ImageIO.write(resultImg, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorImg, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorImg, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	

	private void runBruteForceUnique2(ResultXML testResultXML, ImageRefList testRefListXML){
		BruteForce_Unique2 bf = new BruteForce_Unique2(testResultXML, testRefListXML);
		bf.loop();
		
		double[][] errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		BufferedImage errorImg = ImageTools.generateErrorLinImage(testResultXML, errorSq);
		BufferedImage logErrorImg = ImageTools.generateErrorLogImage(testResultXML, errorSq);
		BufferedImage resultImg = ImageTools.generateResultImage(testRefListXML, testResultXML);
		try {
			System.out.println("(BFU2) Starting writing images to file.");
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU2_" + testResultXML.name + ".png");
		    ImageIO.write(resultImg, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU2_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorImg, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU2_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorImg, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	

	private void runBruteForceUnique3(ResultXML testResultXML, ImageRefList testRefListXML){
		BruteForce_Unique3 bf = new BruteForce_Unique3(testResultXML, testRefListXML);
		bf.loop();
		
		double[][] errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		BufferedImage errorImg = ImageTools.generateErrorLinImage(testResultXML, errorSq);
		BufferedImage logErrorImg = ImageTools.generateErrorLogImage(testResultXML, errorSq);
		BufferedImage resultImg = ImageTools.generateResultImage(testRefListXML, testResultXML);
		try {
			System.out.println("(BFU3) Starting writing images to file.");
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU3_" + testResultXML.name + ".png");
		    ImageIO.write(resultImg, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU3_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorImg, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU3_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorImg, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}


	private void runBruteForceUnique4(ResultXML testResultXML, ImageRefList testRefListXML){
		BruteForce_Unique4 bf = new BruteForce_Unique4(testResultXML, testRefListXML);
		bf.loop();
		
		double[][] errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		BufferedImage errorImg = ImageTools.generateErrorLinImage(testResultXML, errorSq);
		BufferedImage logErrorImg = ImageTools.generateErrorLogImage(testResultXML, errorSq);
		BufferedImage resultImg = ImageTools.generateResultImage(testRefListXML, testResultXML);
		try {
			System.out.println("(BFU4) Starting writing images to file.");
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU4_" + testResultXML.name + ".png");
		    ImageIO.write(resultImg, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU4_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorImg, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\BFU4_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorImg, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private void runCheckAll(ResultXML testResultXML, ImageRefList testRefListXML){
		Algorithm alg = new CheckAll(testResultXML, testRefListXML);
		alg.run();
		
		double[][] errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		BufferedImage errorOfBF = ImageTools.generateErrorLinImage(testResultXML, errorSq);
		BufferedImage logErrorOfBF = ImageTools.generateErrorLogImage(testResultXML, errorSq);
		BufferedImage resultOfBF = ImageTools.generateResultImage(testRefListXML, testResultXML);
		try {
			System.out.println("(CA) Starting writing images to file.");
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\CA_" + testResultXML.name + ".png");
		    ImageIO.write(resultOfBF, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\CA_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorOfBF, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\CA_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorOfBF, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		

		try {
			writeUnicityMap("CA_" + testResultXML.name, testResultXML, testRefListXML);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
		
	private void runCheckAllROUnique(ResultXML testResultXML, ImageRefList testRefListXML){
		Algorithm alg = new CheckAllRO_Unique(testResultXML, testRefListXML);
		alg.run();
		
		double[][] errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		BufferedImage errorOfBF = ImageTools.generateErrorLinImage(testResultXML, errorSq);
		BufferedImage logErrorOfBF = ImageTools.generateErrorLogImage(testResultXML, errorSq);
		BufferedImage resultOfBF = ImageTools.generateResultImage(testRefListXML, testResultXML);
		try {
			System.out.println("(CA_RO_U) Starting writing images to file.");
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\CA_RO_U_" + testResultXML.name + ".png");
		    ImageIO.write(resultOfBF, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\CA_RO_U_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorOfBF, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\CA_RO_U_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorOfBF, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private void runGA(ResultXML testResultXML, ImageRefList testRefListXML){
		CollageGA ga = new CollageGA(
				testResultXML.grid.size.w*testResultXML.grid.size.h,	//chromLength
				30,					//populationSize
				0.9f,				//pCrossover
				0.3f,				//lCrossover (fraction)
				0.5f,				//pMutate
				0f,					//pMutateRepeat
				2,					//nElitism
				100000,				//fitnessGoal
				testRefListXML,		//ImageRefList
				testResultXML		//ResultXML
				);
		ga.setMaxExecTime(60);
//		ga.setMaxNumIter(2000);
		ga.run();
		
	
		double[][] ga_errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		BufferedImage errorOfGA = ImageTools.generateErrorLinImage(testResultXML, ga_errorSq);
		BufferedImage logErrorOfGA = ImageTools.generateErrorLogImage(testResultXML, ga_errorSq);
		BufferedImage resultOfGA = ImageTools.generateResultImage(testRefListXML, testResultXML);
		try {
			System.out.println("(GA) Starting writing images to file.");
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_" + testResultXML.name + ".png");
		    ImageIO.write(resultOfGA, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorOfGA, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorOfGA, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}

		try {
			writeUnicityMap("GA_" + testResultXML.name, testResultXML, testRefListXML);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
		
	private void runGAIndi(ResultXML testResultXML, ImageRefList testRefListXML){
		CollageGA_indi ga = new CollageGA_indi(
				testResultXML.grid.size.w*testResultXML.grid.size.h,	//chromLength
				30,					//populationSize
				0.8f,				//pCrossover
				0.4f,				//lCrossover
				0.5f,				//pMutate
				0f,					//pMutateRepeat
				10,					//nElitism
				100000d,			//fitnessGoal
				0.01f,				//pExtinction
				true,				//applyRelativeExtinction
				1e-6d,				//extinctErrorIniThreshold
				1e-5d,				//extinctErrorThreshold
				1e-3d,				//extinctErrorRelThreshold
				50,					//nIniExtinctionWipes
				testRefListXML,		//ImageRefList
				testResultXML		//ResultXML
				);
				
		ga.setMaxExecTime(300);
//		ga.setMaxNumIter(2000);
		ga.run();
		
	
		double[][] ga_errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		BufferedImage errorOfGA = ImageTools.generateErrorLinImage(testResultXML, ga_errorSq);
		BufferedImage logErrorOfGA = ImageTools.generateErrorLogImage(testResultXML, ga_errorSq);
		BufferedImage resultOfGA = ImageTools.generateResultImage(testRefListXML, testResultXML);
		try {
			System.out.println("(GA_I) Starting writing images to file.");
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_I_" + testResultXML.name + ".png");
		    ImageIO.write(resultOfGA, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_I_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorOfGA, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_I_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorOfGA, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		try {
			writeUnicityMap("GA_I_" + testResultXML.name, testResultXML, testRefListXML);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	

	private void runGAIndiSmartFitness(ResultXML testResultXML, ImageRefList testRefListXML){
		CollageGA_indi_smartFitness ga = new CollageGA_indi_smartFitness(
				testResultXML.grid.size.w*testResultXML.grid.size.h,	//chromLength
				30,					//populationSize
				0.8f,				//pCrossover
				0.4f,				//lCrossover
				0.5f,				//pMutate
				0f,					//pMutateRepeat
				10,					//nElitism
				100000d,			//fitnessGoal
				0.01f,				//pExtinction
				true,				//applyRelativeExtinction
				1e-6d,				//extinctErrorIniThreshold
				1e-5d,				//extinctErrorThreshold
				1e-3d,				//extinctErrorRelThreshold
				50,					//nIniExtinctionWipes
				testRefListXML,		//ImageRefList
				testResultXML		//ResultXML
				);
		
		ga.setMaxExecTime(60);
//		ga.setMaxNumIter(2000);
		ga.run();
		
	
		double[][] ga_errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		BufferedImage errorOfGA = ImageTools.generateErrorLinImage(testResultXML, ga_errorSq);
		BufferedImage logErrorOfGA = ImageTools.generateErrorLogImage(testResultXML, ga_errorSq);
		BufferedImage resultOfGA = ImageTools.generateResultImage(testRefListXML, testResultXML);
		try {
			System.out.println("(GA_I) Starting writing images to file.");
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_I_SF_" + testResultXML.name + ".png");
		    ImageIO.write(resultOfGA, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_I_SF_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorOfGA, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_I_SF_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorOfGA, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		try {
			writeUnicityMap("GA_I_SF_" + testResultXML.name, testResultXML, testRefListXML);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	

	

	private void runGAUnique(ResultXML testResultXML, ImageRefList testRefListXML){
		CollageGA_Unique ga = new CollageGA_Unique(
				testResultXML.grid.size.w*testResultXML.grid.size.h,	//chromLength
				30,					//populationSize
				0.9f,				//pCrossover
				0.4f,				//pMutate
				0f,					//pMutateRepeat
				2,					//nElitism
				100000,				//fitnessGoal
				testRefListXML,		//ImageRefList
				testResultXML		//ResultXML
				);
		ga.setMaxExecTime(60);
//		ga.setMaxNumIter(2000);
		ga.run();
		
	
		double[][] ga_errorSq = CollageAlgorithmUtils.getErrorArray(testResultXML,testRefListXML);
		
	//Output:
		System.out.println("(GA_U) Starting to generate images.");
		BufferedImage errorOfGA = ImageTools.generateErrorLinImage(testResultXML, ga_errorSq);
		BufferedImage logErrorOfGA = ImageTools.generateErrorLogImage(testResultXML, ga_errorSq);
		BufferedImage resultOfGA = ImageTools.generateResultImage(testRefListXML, testResultXML);
		System.out.println("(GA_U) Starting writing images to file.");
		try {
			
		    File outputfile;
	
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_U_" + testResultXML.name + ".png");
		    ImageIO.write(resultOfGA, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_U_" + testResultXML.name + "_errorLin.png");
		    ImageIO.write(errorOfGA, "png", outputfile);
		    
		    outputfile = new File(game.getGlobalVars().workspace + "\\GA_U_" + testResultXML.name + "_errorLog.png");
		    ImageIO.write(logErrorOfGA, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	//TODO: Think what to do? Can we use the unicity to define a smarter fitness? I.e., have a distance between non-unique images?
	
	private void writeUnicityMap(String name, ResultXML result, ImageRefList refList) throws IOException{
	    File outputfile = new File(game.getGlobalVars().workspace + "\\" + name + "_unicity.png");
	    BufferedImage img = ImageTools.generateUnicityImage(result, refList);
	    if(img!=null)
	    ImageIO.write(img, "png", outputfile);
	}
	
	
	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void present(float deltaTime) {
		// TODO Auto-generated method stub
//        game.getGraphics().clear(0xff000000);
//		
//		game.getGraphics().drawText("EditorScreen", 50, 50, 0xffffffff);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
