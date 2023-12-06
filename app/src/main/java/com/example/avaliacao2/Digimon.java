package com.example.avaliacao2;

import java.util.Objects;

public class Digimon {

    private String nome;
    private String level;
    private String imgUrl;

    public Digimon() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Digimon)) return false;
        Digimon digimon = (Digimon) o;
        return getNome().equals(digimon.getNome()) && getLevel().equals(digimon.getLevel()) && getImgUrl().equals(digimon.getImgUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNome(), getLevel(), getImgUrl());
    }

    public Digimon(String nome, String level, String imgUrl) {
        this.nome = nome;
        this.level = level;
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Digimon{" +
                "nome='" + nome + '\'' +
                ", level='" + level + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
