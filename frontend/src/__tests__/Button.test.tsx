import React from "react";
import { render, fireEvent } from "@testing-library/react-native";
import { Button } from "../components/ui/Button";

describe("Button component", () => {
  it("renders the label correctly", () => {
    const onPressMock = jest.fn();
    const { getByText } = render(
      <Button label="Click Me" onPress={onPressMock} />
    );

    const buttonElement = getByText("Click Me");
    expect(buttonElement).toBeTruthy();

    fireEvent.press(buttonElement);
    expect(onPressMock).toHaveBeenCalledTimes(1);
  });

  it("disables button when loading", () => {
    const onPressMock = jest.fn();
    const { queryByText } = render(
      <Button label="Click Me" onPress={onPressMock} loading={true} />
    );

    expect(queryByText("Click Me")).toBeNull();
  });
});
