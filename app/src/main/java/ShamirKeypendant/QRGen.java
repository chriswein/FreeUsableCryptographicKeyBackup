/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShamirKeypendant;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.EnumMap;
import java.util.Map;

/**
 * Helper class for returning a QR-Code as an image
 * @author Christoph
 */
public class QRGen {

    /**
     * Returns a QR-Code as an image
     * @param url_s A url you want to encode as a qrcode
     * @return Image of the QR-Code
     */
    public static Bild getCode(String url_s){
        	String myCodeText = url_s;
		int size = 400; 
		Bild result = null;
		try {
			Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			
			hintMap.put(EncodeHintType.MARGIN, 4); 
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size,
					size, hintMap);
			int bitmatrix_width = byteMatrix.getWidth();
			Bild image = new AndroidBild(bitmatrix_width, bitmatrix_width);

			image.drawRect(0, 0, bitmatrix_width, bitmatrix_width, 0xffffff);

			int bits = 0;
 
			for (int i = 0; i < bitmatrix_width; i++) {
				for (int j = 0; j < bitmatrix_width; j++) {
					if (byteMatrix.get(i, j)) {
						boolean b = byteMatrix.get(i,j);
						bits++;
						image.drawRect(i, j, 1, 1,0x000000);

					}
				}
			}
			result = image;
		} catch (WriterException e) {
			System.err.println(e.toString());
		}  
        return result;
    }
}
