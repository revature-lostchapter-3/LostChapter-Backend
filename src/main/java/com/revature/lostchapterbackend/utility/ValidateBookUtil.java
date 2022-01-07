package com.revature.lostchapterbackend.utility;

import java.security.InvalidParameterException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.revature.lostchapterbackend.dao.BookDAO;
import com.revature.lostchapterbackend.dao.GenreDAO;
import com.revature.lostchapterbackend.dto.AddOrUpdateBookDTO;
import com.revature.lostchapterbackend.exceptions.GenreNotFoundException;
import com.revature.lostchapterbackend.exceptions.ISBNAlreadyExists;


public class ValidateBookUtil {

	@Autowired
	GenreDAO gd;

	@Autowired
	BookDAO bd;

	public void validateBookInput(AddOrUpdateBookDTO dto)
			throws GenreNotFoundException, InvalidParameterException, ISBNAlreadyExists {
		/*-
		 * 	blank inputs
		 */
		boolean blankInputs = false;
		StringBuilder blankInputStrings = new StringBuilder();

		if (StringUtils.isBlank(dto.getISBN())) {
			blankInputStrings.append("ISBN");
			blankInputs = true;
		}

		if (StringUtils.isBlank(dto.getBookName())) {
			if (blankInputs) {
				blankInputStrings.append(", book name");
				blankInputs = true;
			} else {
				blankInputStrings.append("Book name");
				blankInputs = true;

			}

		}

		if (StringUtils.isBlank(dto.getSynopsis())) {
			if (blankInputs) {
				blankInputStrings.append(", synopsis");
				blankInputs = true;
			} else {
				blankInputStrings.append("Synopsis");
				blankInputs = true;

			}

		}

		if (StringUtils.isBlank(dto.getAuthor())) {
			if (blankInputs) {
				blankInputStrings.append(", author");
				blankInputs = true;
			} else {
				blankInputStrings.append("Author");
				blankInputs = true;

			}

		}
		// String genre = Integer.toString();
		if (dto.getGenre() == 0) {
			if (blankInputs) {
				blankInputStrings.append(", genre");
				blankInputs = true;
			} else {
				blankInputStrings.append("Genre");
				blankInputs = true;

			}

		}

		String quantity = Integer.toString(dto.getQuantity());
		if (StringUtils.isBlank(quantity)) {
			if (blankInputs) {
				blankInputStrings.append(", quantity");
				blankInputs = true;
			} else {
				blankInputStrings.append("Quantity");
				blankInputs = true;

			}

		}

		String year = Integer.toString(dto.getYear());
		if (StringUtils.isBlank(year)) {
			if (blankInputs) {
				blankInputStrings.append(", year");
				blankInputs = true;
			} else {
				blankInputStrings.append("Year");
				blankInputs = true;

			}

		}

		if (StringUtils.isBlank(dto.getEdition())) {
			if (blankInputs) {
				blankInputStrings.append(", edition");
				blankInputs = true;
			} else {
				blankInputStrings.append("Edition");
				blankInputs = true;

			}

		}

		if (StringUtils.isBlank(dto.getPublisher())) {
			if (blankInputs) {
				blankInputStrings.append(", publisher");
				blankInputs = true;
			} else {
				blankInputStrings.append("Publisher");
				blankInputs = true;

			}

		}

		String sale = Boolean.toString(dto.isSaleIsActive());
		if (StringUtils.isBlank(sale)) {
			if (blankInputs) {
				blankInputStrings.append(", sale");
				blankInputs = true;
			} else {
				blankInputStrings.append("Sale");
				blankInputs = true;

			}

		} else {
			if ((dto.isSaleIsActive() == true)) {
				if (dto.getSaleDiscountRate() == 0.0) {
					if (blankInputs) {
						blankInputStrings.append(", sale discount rate");
						blankInputs = true;
					} else {
						blankInputStrings.append("Sale discount rate");
						blankInputs = true;

					}

				}

			}
		}

		String price = Double.toString(dto.getBookPrice());
		if (StringUtils.isBlank(price)) {
			if (blankInputs) {
				blankInputStrings.append(", price");
				blankInputs = true;
			} else {
				blankInputStrings.append("Price");
				blankInputs = true;
			}

		}

		if (StringUtils.isBlank(dto.getBookImage())) {
			if (blankInputs) {
				blankInputStrings.append(", image");
				blankInputs = true;
			} else {
				blankInputStrings.append("Image");
				blankInputs = true;
			}

		}

		if (blankInputs) {
			blankInputStrings.append(" cannot be blank.");
			throw new InvalidParameterException(blankInputStrings.toString());
		}

		/*-
		 *  check if isbn already exist
		 */
		if (bd.findByISBN(dto.getISBN()).isPresent()) {
			throw new ISBNAlreadyExists("ISBN already used for another book");
		}

		/*-
		 *  validate int inputs
		 */
		boolean lessThanZeroBoolean = false;
		StringBuilder lessThanZeroString = new StringBuilder();

		if (dto.getQuantity() <= 0) {
			lessThanZeroString.append("Quantity");
			lessThanZeroBoolean = true;
		}

		if (dto.getYear() <= 0) {
			if (lessThanZeroBoolean) {
				lessThanZeroString.append(", year");
				lessThanZeroBoolean = true;
			} else {
				lessThanZeroString.append("Year");
				lessThanZeroBoolean = true;
			}

		}

		if (dto.getBookPrice() <= 0.0) {
			if (lessThanZeroBoolean) {
				lessThanZeroString.append(", book price");
				lessThanZeroBoolean = true;
			} else {
				lessThanZeroString.append("Book price");
				lessThanZeroBoolean = true;
			}

		}

		if (lessThanZeroBoolean) {
			lessThanZeroString.append(" cannot be less than or equal to 0.");
			throw new InvalidParameterException(lessThanZeroString.toString());
		}

		/*-
		 *  validate synopsis length
		 */
		if (dto.getSynopsis().length() > 800) {
			throw new InvalidParameterException("Synopsis cannot be longer than 800 characters.");
		}

		/*-
		 *  validate genre
		 */
		if (!gd.findById(dto.getGenre()).isPresent()) {
			throw new GenreNotFoundException("Genre doesn't exist");
		}

	}

}
