module.exports = {
  root: true,
  extends: ["universe/native"],
  ignorePatterns: ["node_modules/", ".expo/", "dist/"],
  rules: {
    "import/order": ["warn", { alphabetize: { order: "asc" } }],
  },
};
