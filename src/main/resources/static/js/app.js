/* NÚCLEO — comportamentos de interface (vanilla JS) */
(function () {
  "use strict";

  /* ---------- Tema (claro / escuro / sistema) ---------- */
  const root = document.documentElement;
  const STORE = "nucleo-theme";
  const saved = localStorage.getItem(STORE);
  if (saved === "light" || saved === "dark")
    root.setAttribute("data-theme", saved);

  function currentResolved() {
    const attr = root.getAttribute("data-theme");
    if (attr === "light" || attr === "dark") return attr;
    return window.matchMedia("(prefers-color-scheme: dark)").matches
      ? "dark"
      : "light";
  }
  window.toggleTheme = function () {
    const next = currentResolved() === "dark" ? "light" : "dark";
    root.setAttribute("data-theme", next);
    localStorage.setItem(STORE, next);
  };

  /* ---------- Menu mobile ---------- */
  window.addEventListener("DOMContentLoaded", function () {
    const burger = document.querySelector(".nav-burger");
    const links = document.querySelector(".nav-links");
    const scrim = document.querySelector(".nav-scrim");
    function close() {
      burger && burger.classList.remove("open");
      links && links.classList.remove("open");
      scrim && scrim.classList.remove("open");
      document.body.style.overflow = "";
    }
    function open() {
      burger.classList.add("open");
      links.classList.add("open");
      scrim && scrim.classList.add("open");
      document.body.style.overflow = "hidden";
    }
    if (burger && links) {
      burger.addEventListener("click", function () {
        links.classList.contains("open") ? close() : open();
      });
      scrim && scrim.addEventListener("click", close);
      links
        .querySelectorAll("a")
        .forEach((a) => a.addEventListener("click", close));
    }

    /* ---------- Nav compacta ao rolar ---------- */
    const nav = document.querySelector(".nav");
    if (nav) {
      const onScroll = () =>
        nav.classList.toggle("scrolled", window.scrollY > 220);
      onScroll();
      window.addEventListener("scroll", onScroll, { passive: true });
    }

    /* ---------- Reveal no scroll ---------- */
    const reveals = document.querySelectorAll(".reveal");
    if ("IntersectionObserver" in window && reveals.length) {
      const io = new IntersectionObserver(
        (entries) => {
          entries.forEach((e, i) => {
            if (e.isIntersecting) {
              e.target.classList.add("in");
              io.unobserve(e.target);
            }
          });
        },
        { rootMargin: "0px 0px -8% 0px", threshold: 0.08 },
      );
      reveals.forEach((el, i) => {
        el.style.transitionDelay = (i % 4) * 60 + "ms";
        io.observe(el);
      });
    } else {
      reveals.forEach((el) => el.classList.add("in"));
    }
  });

  /* ---------- Data por extenso (topbar) ---------- */
  window.addEventListener("DOMContentLoaded", function () {
    const el = document.querySelector("[data-today]");
    if (el) {
      const d = new Date();
      const fmt = new Intl.DateTimeFormat("pt-BR", {
        weekday: "long",
        day: "2-digit",
        month: "long",
        year: "numeric",
      });
      el.textContent = fmt.format(d);
    }
  });
})();
