package com.example.zx50.myradar.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AESencryptorTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void encrypt() {
        String content = "who are you?";
        String key = "123";
        String encrypt = AESencryptor.encrypt(content, key);
        System.out.println(content);
        System.out.println(encrypt);
        System.out.println(AESencryptor.decrypt(encrypt, key));
    }

    @Test
    public void decrypt() {
    }

    @Test
    public void parseByte2HexStr() {
    }

    @Test
    public void parseHexStr2Byte() {
    }
}