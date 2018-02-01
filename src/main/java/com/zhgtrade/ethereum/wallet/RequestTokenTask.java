package com.zhgtrade.ethereum.wallet;

import com.zhgtrade.ethereum.wallet.constant.TokenConstant;
import com.zhgtrade.ethereum.wallet.model.Token;
import com.zhgtrade.ethereum.wallet.utils.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Author:xxp
 * Company:招股金服
 * Date:2017/8/17
 * Desc:
 */
public class RequestTokenTask implements Runnable {
    private Logger log = Logger.getLogger(getClass().getName());

    private static final String requestTokenUrl = ConfigUtils.getConfig("request.token.url");
    private static final String notifyUrl = ConfigUtils.getConfig("request.dais.url");

    private void getTokenParam() {
        try {
            List<Token> tokens = TokenUtils.getTokens("token");
            for (Token token : tokens) {
                String response = HttpClientUtil.doGet(requestTokenUrl+token.getContractAddress());
                response = response.trim();
                String name = response.substring(response.indexOf("<title>")+7,response.indexOf("("));
                token.setName(name.replace("\n","").trim().replace("\t","").trim().replace("\r","").trim());
                TokenUtils.updateToken(token.getId(),token.getName());
                Map map = new HashMap();
                String shortName = response.substring(response.indexOf("(")+1,response.indexOf(")"));
                map.put("contractAddress",token.getContractAddress());
                map.put("accessPassword",token.getAccessPassword());
                map.put("name",token.getName());
                map.put("shortName",shortName.trim());
                map.put("fee",TokenConstant.require_fee_ether.toString());
                HttpClientUtil.doPost(notifyUrl,map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        for (;;) {
            getTokenParam();
            try {
                Thread.sleep(1000 * 3);
            } catch (Exception e) {

            }
        }
    }

    public static void main(String[] args) {
        try {
             String response = HttpClientUtil.doGet(requestTokenUrl+"0x6331f5219fcacccc917e27d1ac7791cdd6df3668");
            response = response.trim();
            String name = response.substring(response.indexOf("<title>")+7,response.indexOf("("));

            String shortName = response.substring(response.indexOf("(")+1,response.indexOf(")"));
            System.out.println(shortName + "" +name.replace("\n","").trim().replace("\t","").trim().replace("\r","").trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
