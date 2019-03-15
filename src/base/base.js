/**
 * @returns <E extends Element = Element>(selectors: string) =>  E | null
 */
export const query = document.querySelector.bind(document);
/**
 * @returns <E extends Element = Element>(selectors: string) => NodeListOf<E>
 */
export const queryAll = document.querySelectorAll.bind(document);
