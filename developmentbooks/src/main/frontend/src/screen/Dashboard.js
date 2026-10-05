import React, { useEffect, useState, Fragment } from "react";
import "../styles/dashboard.css";
import axios from "axios";
import Product from "../components/Product";
import { getFormattedPrice } from "../utils/helpers";

const Dashboard = () => {
  const [books, setBooks] = useState([]);
  const [discounts, setDiscounts] = useState({});
  const [cart, setCart] = useState([]);
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [price, setPrice] = useState(null);

  const fetchBooks = () => {
    axios.get("/api/developmentbooks/getBooks").then((response) => {
      setBooks(response.data);
    });
  };

  const fetchDiscounts = () => {
    axios.get("/api/developmentbooks/getDiscountDetails").then((response) => {
      setDiscounts(response.data);
    });
  };

  useEffect(() => {
    fetchBooks();
    fetchDiscounts();
  }, []);

  const addToCart = (id, value) => {
    const check_index = cart.findIndex((item) => item.id === id);
    if (check_index !== -1) {
      if (value === 0) {
        cart.splice(check_index, 1);
      } else {
        cart[check_index].quantity = value;
      }
    } else {
      cart.push({ id: id, quantity: value });
    }

    updateButtonStatus(cart);
    setCart(cart);
  };

  const updateButtonStatus = (cart) => {
    setIsButtonDisabled(cart.length === 0);
  };

  const fetchPriceForCart = () => {
    axios
      .post("/api/developmentbooks/fetchPriceSummary", cart)
      .then((response) => {
        setPrice(response.data);
      });
  };

  const getPriceClassName = (price) => {
    if (price.totalDiscount > 0) {
      return "line_through";
    }
    return "";
  };

  const isProductAndDiscountDetailsAvailable = () => {
    return books.length !== 0 && discounts !== {};
  };

  return (
    <div className="container">
      <div className="header">
        <h1 className="title">Development Books</h1>
      </div>
      <div className="content">
        {!isProductAndDiscountDetailsAvailable() && (
          <h1 className="loading-indicator">Loading...</h1>
        )}
        {isProductAndDiscountDetailsAvailable() && (
          <Fragment>
            <div className="products">
              {books.map((book) => {
                return (
                  <Fragment key={book.id}>
                    <Product book={book} addToCart={addToCart} />
                  </Fragment>
                );
              })}
            </div>
            <div className="discount-cart-panel">
              <ul className="discount">
                {Object.keys(discounts).map((distinctBooks) => (
                  <li key={distinctBooks}>
                    Buy {distinctBooks} different books and get{" "}
                    {discounts[distinctBooks]}% discount.
                  </li>
                ))}
              </ul>
              <div className="cart">
                <button
                  className="calculate-price-btn"
                  disabled={isButtonDisabled}
                  onClick={fetchPriceForCart}
                >
                  Calculate Price
                </button>
                {price !== null && (
                  <Fragment>
                    <div className="price">
                      Price:{" "}
                      <span className={getPriceClassName(price)}>
                        {getFormattedPrice(price.actualPrice)}
                      </span>
                    </div>
                    <div className="discount-summary">
                      {price.listOfBookGroups.map((listOfBooks, summaryKey) => {
                        if (listOfBooks.discount > 0) {
                          return (
                            <div
                              className="discount-item-summary"
                              key={summaryKey}
                            >
                              Discount of {listOfBooks.discountPercentage}%
                              applied on {listOfBooks.numberOfBooks} books
                            </div>
                          );
                        } else {
                          return (
                            <div
                              className="undiscounted-item-summary"
                              key={summaryKey}
                            >
                              No Discount applied on {listOfBooks.numberOfBooks}{" "}
                              book(s)
                            </div>
                          );
                        }
                      })}
                    </div>
                    {price.totalDiscount > 0 && (
                      <div className="discounted-price">
                        Discounted Price: {getFormattedPrice(price.finalPrice)}
                      </div>
                    )}
                  </Fragment>
                )}
              </div>
            </div>
          </Fragment>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
