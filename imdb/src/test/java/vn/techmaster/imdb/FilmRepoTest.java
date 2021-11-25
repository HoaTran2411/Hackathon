package vn.techmaster.imdb;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import vn.techmaster.imdb.model.Film;
import vn.techmaster.imdb.repository.FilmRepository;

@SpringBootTest
class FilmRepoTest {
	@Autowired
	private FilmRepository filmRepo;

	@Test
	public void getAll() {
		List<Film> filmList = filmRepo.getAll();
		filmList.forEach(System.out::println);
		assertThat(filmList.size()).isGreaterThanOrEqualTo(20);
	}

	@Test
	public void getFilmsByCountry() {
		var country_film = filmRepo.getFilmByCountry();
		System.out.println("Phân loại danh sách film theo quốc gia sản xuất: ");
		country_film.entrySet().forEach(entry -> {
			System.out.println(entry.getKey() + ": ");
			entry.getValue().forEach(System.out::println);
		});

		System.out.println("Danh sách countries: ");
		List<String> countries = filmRepo.getAll().stream().map(Film::getCountry).distinct().peek(System.out::println)
				.toList();

		assertThat(countries).containsAll(country_film.keySet());
	}

	@Test
	public void getcountryMakeMostFilms() {
		var country_filmQuantity = filmRepo.getcountryMakeMostFilms();
		System.out.println("Nước sản xuất nhiều phim nhất với số lượng phim tương ứng: ");
		System.out.println(country_filmQuantity.getKey() + " - " + country_filmQuantity.getValue());
		assertThat(country_filmQuantity.getValue()).isGreaterThan(5);
	}

	@Test
	public void yearMakeMostFilms() {
		var year_filmQuantity = filmRepo.yearMakeMostFilms();
		System.out.println("Năm sản xuất phim nhiều nhất với số lượng phim tương ứng: ");
		System.out.println(year_filmQuantity.getKey() + " - " + year_filmQuantity.getValue());
		assertThat(year_filmQuantity.getValue()).isGreaterThanOrEqualTo(4);
	}

	@Test
	public void getAllGeneres() {
		System.out.println("Danh sách tất cả các thể loại phim: ");
		var listGenres = filmRepo.getAllGeneres();
		System.out.println(listGenres);
		assertThat(listGenres).contains("thriller", "series", "love", "sex", "fiction", "documentary", "adventure",
				"war", "western", "cartoon");
	}

	@Test
	public void getFilmsMadeByCountryFromYearToYear() {
		System.out.println("Danh sách phim thỏa mãn điều kiện: ");
		filmRepo.getFilmsMadeByCountryFromYearToYear("brazil", 1980, 1987).stream().forEach(System.out::println);

	}

	@Test
	public void categorizeFilmByGenere() {
		System.out.println("Phân loại phim theo thể loại: ");
		var genre_films = filmRepo.categorizeFilmByGenere();
		genre_films.entrySet().forEach(entry -> {
			System.out.println(entry.getKey() + " : ");
			entry.getValue().forEach(System.out::println);
		});

		// danh sách các thể loại phim:
		var listGenres = filmRepo.getAllGeneres();
		assertThat(genre_films.keySet()).containsAll(listGenres);

	}

	@Test
	public void top5HighMarginFilms() {
		System.out.println("Top 5 film có lãi lớn nhất: ");
		filmRepo.top5HighMarginFilms().stream().forEach(System.out::println);
		assertThat(filmRepo.top5HighMarginFilms()).hasSize(5)
				.isSortedAccordingTo(Comparator.comparing(Film::getMargin).reversed());
	}

	@Test
	public void top5HighMarginFilmsIn1990to2000() {
		System.out.println("Top 5 film từ năm 1990 đến 2000 có lãi lớn nhất: ");
		var top5Films = filmRepo.top5HighMarginFilmsIn1990to2000();
		top5Films.stream().forEach(System.out::println);
		assertThat(top5Films).hasSize(5).isSortedAccordingTo(Comparator.comparing(Film::getMargin).reversed());
	}

	@Test
	public void ratioBetweenGenere() {
		String genreA = "Science";
		String genreB = "Documentary";

		System.out.println("Tỷ lệ phim giữa 2 thể loại: ");
		Double ratio = filmRepo.ratioBetweenGenere(genreA, genreB);

		int quantityOfFilmGenreA = filmRepo.getAll().stream()
				.filter(film -> film.getGeneres().contains(genreA.toLowerCase())).toList().size();
		int quantityOfFilmGenreB = filmRepo.getAll().stream()
				.filter(film -> film.getGeneres().contains(genreB.toLowerCase())).toList().size();

		System.out.println(ratio);
		assertThat(ratio).isEqualTo(quantityOfFilmGenreA / quantityOfFilmGenreB);
	}

	@Test
	public void top5FilmsHighRatingButLowMargin() {
		var top5Films = filmRepo.top5FilmsHighRatingButLowMargin();
		top5Films.stream().forEach(System.out::println);
		assertThat(top5Films).hasSize(5)
				.isSortedAccordingTo(Comparator.comparing(Film::getRating).reversed().thenComparing(Film::getMargin));
	}

}
