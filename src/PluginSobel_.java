/*Desenvolver um plugin para aplicar o filtro não linear de Sobel na vertical e na horizontal em uma imagem corrente.
Apresentar os resultados em duas novas imagens.
Em seguida deverá ser criada e apresentada uma terceira imagem com a junção dos dois resultados.
(Fórmula presente no último slide da aula)*/

import ij.plugin.PlugIn;
import ij.ImagePlus;
import ij.IJ;
import ij.process.ImageProcessor;

public class PluginSobel_ implements PlugIn{	
	private ImagePlus image;
	private ImageProcessor processorVertical, processorHorizontal, processorOriginal, processorTemp;	
	
	public void run(String arg) {		
		image = IJ.getImage();
		processorOriginal = image.getProcessor();
		processorVertical = image.duplicate().getProcessor();
		processorHorizontal = image.duplicate().getProcessor();
		processorTemp = image.duplicate().getProcessor();
		if (image.getType() != ImagePlus.GRAY8) {
			IJ.error("In order to run this plugin, the image must be Type GRAY8");
		}
		else this.imageFilter();	
	}	
	
	public void imageFilter() {
		int[][] kernelVertical = {{-1,0,1},{-2,0,2},{-1,0,1}};
		int[][] kernelHorizontal = {{1,2,1},{0,0,0},{-1,-2,-1}};
		int width = image.getWidth(), height = image.getHeight();
		ImagePlus vertical = IJ.createImage("Vertical", "8-bit", width, height, 1);
		ImagePlus horizontal = IJ.createImage("Horizontal", "8-bit", width, height, 1);
		double sumVertical, sumHorizontal;
		int[][] sourcePixels = new int[3][3];
		for (int x = 1; x < width-1; x++) {
			for (int y = 1; y < height-1; y++) {
				sumHorizontal = sumVertical = 0;
				for (int i=0;i<3;i++)
					for (int j=0;j<3;j++) {
						sourcePixels[i][j] = processorOriginal.getPixel(x-1+i, y-1+j);
						sumVertical += (double) (sourcePixels[i][j]*kernelVertical[i][j]);
						sumHorizontal += (double) (sourcePixels[i][j]*kernelHorizontal[i][j]);
					}				
				processorVertical.putPixel(x, y, pixelValidation((int) sumVertical));
				processorHorizontal.putPixel(x, y, pixelValidation((int) sumHorizontal));
				processorTemp.putPixel(x, y, pixelValidation((int) Math.sqrt(Math.pow(sumVertical, 2) + Math.pow(sumHorizontal, 2))));
			}
		}
		image.setProcessor(processorTemp);
		vertical.setProcessor(processorVertical);
		horizontal.setProcessor(processorHorizontal);
		image.setTitle("Vertical + Horizontal");
		image.updateAndDraw();
		vertical.show();
		horizontal.show();		
	}
	
	private int pixelValidation(int pixel) {
		if (pixel > 255) {
			return 255;
		}
		else if (pixel < 0) {
			return 0;
		}
		else return pixel;
	}
	
}
	