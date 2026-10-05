import { render, within, fireEvent } from "@testing-library/react";
import Product from "../../components/Product";

const setUp = () => {
  const book = {
    id: 1,
    title: "Clean Code",
    author: "Robert Martin",
    year: 2008,
    price: 50.0,
    imageUrl:
      "https://github.com/stephane-genicot/katas/raw/master/images/Kata_DevelopmentBooks_CleanCode.png?raw=true",
  };

  return render(<Product book={book} addToCart={() => {}} />);
};

describe("Product component", () => {
  test("should display product information", () => {
    const { container } = setUp();

    expect(container.querySelector(".product-image")).toBeVisible();
    expect(container.querySelector(".product-title")).toBeVisible();
    expect(container.querySelector(".author")).toBeVisible();
    expect(container.querySelector(".product-price")).toBeVisible();
  });

  test("should display product price in euro format", () => {
    const { container } = setUp();

    const { getByText } = within(container.querySelector(".product-price"));
    expect(getByText("50,00 €")).toBeInTheDocument();
  });

  test("should display quantity picker", () => {
    const { container } = setUp();

    expect(
      container.querySelector(".product-quantity").firstElementChild
    ).toHaveAttribute("data-quantity", "");
  });

  test("should increase quantity on add button click", () => {
    const { container } = setUp();

    const quantityPicker =
      container.querySelector(".product-quantity").firstElementChild;
    fireEvent(
      quantityPicker.querySelector(".add"),
      new MouseEvent("click", {
        bubbles: true,
        cancelable: true,
      })
    );

    expect(quantityPicker.querySelector("input")).toHaveValue(1);
  });

  test("should reduce quantity on subtract button click", () => {
    const { container } = setUp();

    const quantityPicker =
      container.querySelector(".product-quantity").firstElementChild;
    fireEvent(
      quantityPicker.querySelector(".add"),
      new MouseEvent("click", {
        bubbles: true,
        cancelable: true,
      })
    );
    fireEvent(
      quantityPicker.querySelector(".sub"),
      new MouseEvent("click", {
        bubbles: true,
        cancelable: true,
      })
    );

    expect(quantityPicker.querySelector("input")).toHaveValue(0);
  });
});
