package com.group5.engagement.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.group5.engagement.exception.QrGenerationException;
import com.group5.engagement.service.QrCodeService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    @Override
    public String generateQrBase64(String content) {

        try {
            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix matrix =
                    writer.encode(content, BarcodeFormat.QR_CODE, 250, 250);

            BufferedImage image =
                    new BufferedImage(250, 250, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < 250; x++) {
                for (int y = 0; y < 250; y++) {
                    image.setRGB(
                            x,
                            y,
                            matrix.get(x, y) ? 0x000000 : 0xFFFFFF
                    );
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            return "data:image/png;base64," + Base64.getEncoder()
                    .encodeToString(baos.toByteArray());

        } catch (Exception e) {
            throw new QrGenerationException(
                    "Failed to generate QR code"
            );
        }
    }
}
