package br.com.brunoxkk0.custom;

import org.json.JSONObject;

public class Staff implements IHandler{
    @Override
    public String identifier() {
        return "staff";
    }

    @Override
    public JSONObject process() {
        JSONObject reply = new JSONObject();

        return reply;
    }
}
