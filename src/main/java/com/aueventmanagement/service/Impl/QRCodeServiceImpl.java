package com.aueventmanagement.service.Impl;


import com.aueventmanagement.service.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service

public class QRCodeServiceImpl implements QRCodeService {


    @Override
    public byte[] generateQRCode(String data) {


        try{

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    data,
                    BarcodeFormat.QR_CODE,
                    300,300
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream
                    (bitMatrix,"PNG",outputStream);

            return outputStream.toByteArray();
        } catch (Exception e){
            throw new RuntimeException("Error generating QR Code");
        }
    }
}
