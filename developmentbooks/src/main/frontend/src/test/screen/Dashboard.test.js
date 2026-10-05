import { render, waitFor, fireEvent, getByText } from "@testing-library/react";
import Dashboard from "../../screen/Dashboard";
import axios from "axios";
import getBooksResponse from "./responses/getBooks.json";
import getDiscountDetailsResponse from "./responses/getDiscountDetails.json";

afterEach(() => {
  axios.get.mockClear();
});
jest.mock("axios");

const setUp = () => {
  axios.get.mockImplementation((url) => {
    if (url.indexOf("/api/developmentbooks/getBooks") !== -1) {
      return Promise.resolve({
        status: 200,
        data: getBooksResponse,
      });
    }
    if (url.indexOf("/api/developmentbooks/getDiscountDetails") !== -1) {
      return Promise.resolve({
        status: 200,
        data: getDiscountDetailsResponse,
      });
    }
    return new Promise(() => {});
  });

  return render(<Dashboard />);
};

describe("load development books dashboard", () => {
  test("should display development dashboard title", () => {
    axios.get.mockImplementation(() => new Promise(() => {}));

    const { container } = render(<Dashboard />);

    expect(container.querySelector(".title")).toContainHTML(
      "Development Books"
    );
  });

  test("should call get books API", () => {
    axios.get.mockImplementation(() => new Promise(() => {}));

    render(<Dashboard />);

    expect(axios.get).toHaveBeenCalledWith(
      expect.stringMatching(/\/api\/developmentbooks\/getBooks/)
    );
  });

  test("should display books from get books API response", async () => {
    axios.get.mockImplementation((url) => {
      if (url.indexOf("/api/developmentbooks/getBooks") !== -1) {
        return Promise.resolve({
          status: 200,
          data: getBooksResponse,
        });
      }
      return new Promise(() => {});
    });

    const { container } = render(<Dashboard />);
    const products = await waitFor(() =>
      container.getElementsByClassName("product")
    );

    expect(products.length).toBe(5);
  });

  test("should display loading indicator when there is no data", () => {
    axios.get.mockImplementation(() => new Promise(() => {}));

    const { container } = render(<Dashboard />);

    expect(container.querySelector(".loading-indicator")).toHaveTextContent(
      "Loading..."
    );
  });

  describe("Discount container", () => {
    test("should call get discount details API", () => {
      axios.get.mockImplementation(() => new Promise(() => {}));

      render(<Dashboard />);

      expect(axios.get).toHaveBeenCalledWith(
        expect.stringMatching(/\/api\/developmentbooks\/getDiscountDetails/)
      );
    });

    test("should display discount details", async () => {
      const { container } = setUp();

      const discounts = await waitFor(() => {
        const discountWrapper = container.firstChild.querySelector(
          ".discount-cart-panel ul.discount"
        );
        return discountWrapper.querySelectorAll("li");
      });

      expect(discounts.length).toBe(4);
    });

    test("should display calculate price button", async () => {
      const { container } = setUp();

      await waitFor(() => container.getElementsByClassName("product"));
      expect(container.querySelector(".calculate-price-btn")).toBeVisible();
    });

    test("should display calculate price as disabled by default", async () => {
      const { container } = setUp();

      await waitFor(() => container.getElementsByClassName("product"));

      expect(container.querySelector(".calculate-price-btn")).toBeDisabled();
    });

    test("should enable calculate price button when a product is added to the cart", async () => {
      const { container } = setUp();

      const products = await waitFor(() =>
        container.getElementsByClassName("products")
      );
      fireEvent(
        products[0].querySelector(".add"),
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );

      expect(
        container.querySelector(".calculate-price-btn")
      ).not.toBeDisabled();
    });

    test("should disable the calculate price button when cart is empty", async () => {
      const { container } = setUp();

      const products = await waitFor(() =>
        container.getElementsByClassName("products")
      );
      fireEvent(
        products[0].querySelector(".add"),
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );
      fireEvent(
        products[0].querySelector(".sub"),
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );

      expect(container.querySelector(".calculate-price-btn")).toBeDisabled();
    });

    test("should call fetch price API on calculate price button click", async () => {
      const cart = [{ id: 1, quantity: 1 }];
      const { container } = setUp();
      axios.post.mockImplementation((url) => new Promise(() => {}));

      const products = await waitFor(() =>
        container.getElementsByClassName("products")
      );
      fireEvent(
        products[0].querySelector(".add"),
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );
      fireEvent(
        container.querySelector(".calculate-price-btn"),
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );

      expect(axios.post).toHaveBeenCalledWith(
        expect.stringMatching(/\/api\/developmentbooks\/fetchPriceSummary/),
        cart
      );
    });

    test("should get the price when calculate price button is clicked with a product in cart", async () => {
      const { container } = setUp();
      axios.post.mockImplementation((url) => {
        if (url.indexOf("/api/developmentbooks/fetchPriceSummary") !== -1) {
          return Promise.resolve({
            status: 200,
            data: {
              listOfBookGroups: [
                {
                  listOfbooks: [2],
                  discountPercentage: 0,
                  actualPrice: 50.0,
                  discount: 0.0,
                  numberOfBooks: 1,
                },
              ],
              actualPrice: 50.0,
              totalDiscount: 0.0,
              finalPrice: 50.0,
            },
          });
        }

        return new Promise(() => {});
      });

      await waitFor(() => container.getElementsByClassName("products"));
      fireEvent(
        container.getElementsByClassName("add")[0],
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );
      fireEvent(
        container.querySelector(".calculate-price-btn"),
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );

      await waitFor(() => container.getElementsByClassName("price"));
      const price = container.querySelector(".price");
      expect(getByText(price, "50,00 €")).toBeVisible();
    });

    test("should get discount price on click of calculate price button with different products in cart", async () => {
      const { container } = setUp();
      axios.post.mockImplementation((url) => {
        if (url.indexOf("/api/developmentbooks/fetchPriceSummary") !== -1) {
          return Promise.resolve({
            status: 200,
            data: {
              listOfBookGroups: [
                {
                  listOfbooks: [1, 2],
                  discountPercentage: 5,
                  actualPrice: 100.0,
                  discount: 5.0,
                  numberOfBooks: 2,
                },
              ],
              actualPrice: 100.0,
              totalDiscount: 5.0,
              finalPrice: 95.0,
            },
          });
        }

        return new Promise(() => {});
      });

      await waitFor(() => container.getElementsByClassName("products"));
      fireEvent(
        container.getElementsByClassName("add")[0],
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );
      fireEvent(
        container.getElementsByClassName("add")[1],
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );
      fireEvent(
        container.querySelector(".calculate-price-btn"),
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );

      await waitFor(() => container.getElementsByClassName("discounted-price"));
      const price = container.querySelector(".discounted-price");
      expect(getByText(price, "Discounted Price: 95,00 €")).toBeVisible();
    });

    test("should display the discount summary when discount applied on cart", async () => {
      const { container } = setUp();
      axios.post.mockImplementation((url) => {
        if (url.indexOf("/api/developmentbooks/fetchPriceSummary") !== -1) {
          return Promise.resolve({
            status: 200,
            data: {
              listOfBookGroups: [
                {
                  listOfbooks: [1, 2],
                  discountPercentage: 5,
                  actualPrice: 100.0,
                  discount: 5.0,
                  numberOfBooks: 2,
                },
              ],
              actualPrice: 100.0,
              totalDiscount: 5.0,
              finalPrice: 95.0,
            },
          });
        }

        return new Promise(() => {});
      });

      await waitFor(() => container.getElementsByClassName("products"));
      fireEvent(
        container.getElementsByClassName("add")[0],
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );
      fireEvent(
        container.getElementsByClassName("add")[1],
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );
      fireEvent(
        container.querySelector(".calculate-price-btn"),
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );

      await waitFor(() =>
        container.getElementsByClassName("discount-item-summary")
      );

      expect(container.querySelector(".discount-item-summary")).toContainHTML(
        "Discount of 5% applied on 2 books"
      );
    });

    test("should display the discount summary including books without discounts", async () => {
      const { container } = setUp();
      axios.post.mockImplementation((url) => {
        if (url.indexOf("/api/developmentbooks/fetchPriceSummary") !== -1) {
          return Promise.resolve({
            status: 200,
            data: {
              listOfBookGroups: [
                {
                  listOfbooks: [2, 1],
                  discountPercentage: 5,
                  actualPrice: 100.0,
                  discount: 5.0,
                  numberOfBooks: 2,
                },
                {
                  listOfbooks: [2],
                  discountPercentage: 0,
                  actualPrice: 50.0,
                  discount: 0.0,
                  numberOfBooks: 1,
                },
              ],
              actualPrice: 150.0,
              totalDiscount: 5.0,
              finalPrice: 145.0,
            },
          });
        }

        return new Promise(() => {});
      });

      await waitFor(() => container.getElementsByClassName("products"));
      fireEvent(
        container.getElementsByClassName("add")[0],
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );
      fireEvent(
        container.getElementsByClassName("add")[1],
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );
      fireEvent(
        container.querySelector(".calculate-price-btn"),
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
        })
      );

      await waitFor(() =>
        container.getElementsByClassName("undiscounted-item-summary")
      );

      expect(
        container.querySelector(".undiscounted-item-summary")
      ).toContainHTML("No Discount applied on 1 book(s)");
    });
  });
});
