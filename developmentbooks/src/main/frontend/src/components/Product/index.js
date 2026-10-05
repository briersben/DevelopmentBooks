import React from "react";
import PropTypes from "prop-types";
import { QuantityPicker } from "react-qty-picker";
import { getFormattedPrice } from "../../utils/helpers";

const Product = (props) => {
  const { title, author, imageUrl, price, id, year } = props.book;

  return (
    <div className="product" key={id}>
      <div className="product-image">
        <img src={imageUrl} alt={title} />
      </div>
      <p className="product-title">{title}</p>
      <p className="author-year">
        <span className="author">By {author}</span>
        <span className="year">({year})</span>
      </p>
      <p className="product-price">{getFormattedPrice(price)}</p>
      <span className="product-quantity">
        <QuantityPicker
          min={0}
          smooth
          name="quantity"
          onChange={(value) => props.addToCart(id, value)}
        />
      </span>
    </div>
  );
};

Product.propTypes = {
  book: PropTypes.object.isRequired,
  addToCart: PropTypes.func.isRequired,
};

export default Product;
