/** @type {import('tailwindcss').Config} */

const plugin = require("tailwindcss/plugin");
const { fontFamily } = require("tailwindcss/defaultTheme");
const colors = require("tailwindcss/colors");
// const defaultTheme = require('tailwindcss/defaultTheme')

module.exports = {
  content: [
    "./src/**/*.{html,ts}", // add this line
  ],
  theme: {
    extend: {
      fontFamily: {
        jetbrains: ["JetBrains Mono", ...fontFamily.sans],
      },
      textShadow: {
        sm: "0 1px 2px var(--tw-shadow-color)",
        DEFAULT: "0 2px 4px var(--tw-shadow-color)",
        lg: "0 8px 16px var(--tw-shadow-color)",
      },
      colors: {
        gray: colors.gray,
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          // DocSearch colors
          400: "hsl(var(--primary))",
          500: "hsl(var(--primary))",
          600: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
        },
        tertiary: {
          DEFAULT: "hsl(var(--tertiary))",
          foreground: "hsl(var(--tertiary-foreground))",
        },
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        popover: {
          DEFAULT: "hsl(var(--popover))",
          foreground: "hsl(var(--popover-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
      },
      typography: ({ theme }) => ({
        DEFAULT: {
          css: {
            a: {
              color: theme("colors.primary.400"),
              "&:hover": {
                filter: "brightness(1.50)",
                color: theme("colors.indigo.600"),
              },
              code: { color: theme("colors.pink.600") },
            },
            p: {
              fontSize: "14px",
            },
            "h1,h2": {
              fontWeight: "700",
              letterSpacing: theme("letterSpacing.tight"),
            },
            h3: {
              fontWeight: "600",
            },
            pre: {
              fontSize: "14px !important",
            },
            code: {
              color: theme("colors.indigo.700"),
              fontSize: "inherit",
              fontWeight: "400",
              backgroundColor: theme("colors.primary.foreground"),
              borderWidth: "1px",
              borderColor: theme("colors.border"),
              fontFamily: "consolas",
              borderRadius: "0.25rem",
              padding: "0.2rem 0.25rem",
              margin: "0.2rem",
            },
            "code::before": {
              display: "none",
            },
            "code::after": {
              display: "none",
            },
            img: {
              display: "block",
              borderRadius: "0.5rem",
              borderWidth: "1px",
              borderColor: theme("colors.border"),
              margin: "1.5rem auto !important",
            },
            blockquote: {
              color: theme("colors.muted.foreground"),
              quotes: "none",
              fontStyle: "normal",
              borderLeftColor: theme("colors.indigo.700"),
              borderRightRadius: "0.5rem",
              backgroundColor: theme("colors.indigo.50"),
              paddingTop: "1px",
              paddingBottom: "1px",
            },
            hr: {
              borderColor: theme("colors.violet.200"),
            },
            tr: {
              borderColor: theme("colors.border"),
            },
            thead: {
              borderColor: theme("colors.border"),
            },
            "li::marker": {
              color: theme("colors.muted.foreground"),
            },
          },
        },
      }),
    },
  },
  plugins: [
    require("@tailwindcss/typography"),
    plugin(function ({ matchUtilities, theme }) {
      matchUtilities(
        {
          "text-shadow": (value) => ({
            textShadow: value,
          }),
        },
        { values: theme("textShadow") }
      );
    }),
  ],
};
