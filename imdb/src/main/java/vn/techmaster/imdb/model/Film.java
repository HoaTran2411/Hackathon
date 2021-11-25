package vn.techmaster.imdb.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private int id;
    private String title;
    private int year;
    private String country;
    private double rating;
    private List<String> generes;
    private int cost;
    private int revenue;

    public int getMargin() {
        return (revenue - cost);
    }

    public List<String> getGeneres() {
        return generes.stream().map(String::toLowerCase).filter(genre -> (genre != "" && genre != " ")).toList();
    }

}
