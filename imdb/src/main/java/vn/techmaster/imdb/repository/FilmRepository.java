package vn.techmaster.imdb.repository;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import vn.techmaster.imdb.model.Film;

@Repository
public class FilmRepository implements IFilmRepo {
  private List<Film> films;

  // @Value(tên biến, chỉ định giá trị trong file properties)
  public FilmRepository(@Value("${datafile}") String datafile) {
    try {
      File file = ResourceUtils.getFile("classpath:static/" + datafile);
      ObjectMapper mapper = new ObjectMapper(); // Dùng để ánh xạ cột trong CSV với từng trường trong POJO
      films = Arrays.asList(mapper.readValue(file, Film[].class));
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public List<Film> getAll() {
    return films;
  }

  // Phân loại danh sách film theo quốc gia sản xuất
  @Override
  public Map<String, List<Film>> getFilmByCountry() {
    return films.stream().collect(Collectors.groupingBy(Film::getCountry));
  }

  // Nước nào sản xuất nhiều film nhất, số lượng bao nhiêu?
  @Override
  public Entry<String, Long> getcountryMakeMostFilms() {
    return films.stream().collect(Collectors.groupingBy(Film::getCountry, Collectors.counting())).entrySet().stream()
        .sorted(Comparator.comparing(Map.Entry<String, Long>::getValue).reversed()).limit(1).toList().get(0);
  }

  // Năm nào sản xuất nhiều film nhất, số lượng bao nhiêu?
  @Override
  public Entry<Integer, Long> yearMakeMostFilms() {
    return films.stream().collect(Collectors.groupingBy(Film::getYear, Collectors.counting())).entrySet().stream()
        .sorted(Comparator.comparing(Map.Entry<Integer, Long>::getValue).reversed()).limit(1).toList().get(0);
  }

  // Danh sách tất cả thể loại film
  @Override
  public List<String> getAllGeneres() {
    return films.stream().map(Film::getGeneres).flatMap(Collection::stream).distinct().toList();
  }

  // Tìm film do một quốc gia sản xuất từ năm X đến năm Y
  @Override
  public List<Film> getFilmsMadeByCountryFromYearToYear(String country, int fromYear, int toYear) {
    return films.stream().filter(
        film -> (film.getCountry().equalsIgnoreCase(country)) && film.getYear() >= fromYear && film.getYear() <= toYear)
        .collect(Collectors.toList());
  }

  // Phân loại film theo thể loại
  @Override
  public Map<String, List<Film>> categorizeFilmByGenere() {
    return films.stream().map(Film::getGeneres).flatMap(List::stream).distinct()
        .collect(Collectors.toMap(Function.identity(),
            genre -> films.stream().filter(film -> film.getGeneres().contains(genre)).collect(Collectors.toList())));
  }

  // Top 5 film có lãi lớn nhất margin = revenue - cost
  @Override
  public List<Film> top5HighMarginFilms() {
    return films.stream().sorted(Comparator.comparing(Film::getMargin).reversed()).limit(5).toList();
  }

  // Top 5 film từ năm 1990 đến 2000 có lãi lớn nhất
  @Override
  public List<Film> top5HighMarginFilmsIn1990to2000() {
    return films.stream().filter(film -> (film.getYear() >= 1990 && film.getYear() <= 2000))
        .sorted(Comparator.comparing(Film::getMargin).reversed()).limit(5).toList();
  }

  // Tỷ lệ phim giữa 2 thể loại
  @Override
  public double ratioBetweenGenere(String genreX, String genreY) {
    Map<String, List<Film>> genre_films = categorizeFilmByGenere();
    int quantityOfX = genre_films.get(genreX.toLowerCase()).size();
    int quantityOfY = genre_films.get(genreY.toLowerCase()).size();
    return (double) quantityOfX / quantityOfY;
  }

  // Top 5 film có rating cao nhất nhưng lãi thì thấp nhất (thậm chí lỗ)
  @Override
  public List<Film> top5FilmsHighRatingButLowMargin() {
    return films.stream().sorted(Comparator.comparing(Film::getRating).reversed().thenComparing(Film::getMargin))
        .limit(5).toList();
  }

}
