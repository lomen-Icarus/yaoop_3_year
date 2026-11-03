"""Сборка отчёта по лабораторной работе строго по требованиям практикума ЯООП.

Требования практикума (Praktikum.docx, раздел «Структура отчета», рисунки 1–3):
  * бумага A4 (21 x 29,7 см), книжная ориентация;
  * поля: верхнее 2 см, нижнее 2 см, левое 3 см, правое 1,5 см, переплёт 0;
  * шрифт Times New Roman, 14 пт, цвет чёрный, начертание прямое;
  * абзац основного текста: выравнивание по ширине, отступы слева/справа 0,
    первая строка — отступ 1,25 см, интервал перед/после 0 пт,
    междустрочный — 1,5 строки;
  * все рисунки подписаны («Рисунок N – Название»), на каждый рисунок есть
    ссылка в тексте;
  * титульный лист — по образцу в конце практикума (TNR 12 пт, 1,5 строки).

Структура отчёта: титульный лист; 1. Постановка задачи; 2. Входные,
выходные, расчетные данные; 3. Блок-схема алгоритма; 4. Код программы;
5. Скриншоты с результатами (не менее 3).
"""

from docx import Document
from docx.enum.section import WD_ORIENT
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Pt, RGBColor

FONT = "Times New Roman"
BODY_PT = 14
TITLE_PT = 12          # кегль титульного листа по образцу из практикума
FIRST_LINE = Cm(1.25)
TEXT_WIDTH_CM = 21 - 3 - 1.5


def _set_run_font(run, size_pt, bold=False):
    run.font.name = FONT
    run.font.size = Pt(size_pt)
    run.font.bold = bold
    run.font.italic = False
    run.font.color.rgb = RGBColor(0, 0, 0)
    rpr = run._element.get_or_add_rPr()
    rfonts = rpr.find(qn("w:rFonts"))
    if rfonts is None:
        rfonts = OxmlElement("w:rFonts")
        rpr.insert(0, rfonts)
    for attr in ("w:ascii", "w:hAnsi", "w:cs", "w:eastAsia"):
        rfonts.set(qn(attr), FONT)


def _set_style_font(style, size_pt):
    style.font.name = FONT
    style.font.size = Pt(size_pt)
    style.font.bold = False
    style.font.italic = False
    style.font.color.rgb = RGBColor(0, 0, 0)
    rpr = style.element.get_or_add_rPr()
    rfonts = rpr.find(qn("w:rFonts"))
    if rfonts is None:
        rfonts = OxmlElement("w:rFonts")
        rpr.insert(0, rfonts)
    for attr in ("w:ascii", "w:hAnsi", "w:cs", "w:eastAsia"):
        rfonts.set(qn(attr), FONT)
    for attr in ("w:asciiTheme", "w:hAnsiTheme", "w:cstheme", "w:eastAsiaTheme"):
        rfonts.attrib.pop(qn(attr), None)
    lang = rpr.find(qn("w:lang"))
    if lang is None:
        lang = OxmlElement("w:lang")
        rpr.append(lang)
    lang.set(qn("w:val"), "ru-RU")
    lang.set(qn("w:eastAsia"), "ru-RU")


class Report:
    def __init__(self):
        self.doc = Document()
        self.figure_no = 0
        self.table_no = 0
        self._setup_page()
        self._setup_styles()
        # python-docx создаёт документ с одним пустым абзацем — убираем его
        body = self.doc.element.body
        for p in list(body.findall(qn("w:p"))):
            body.remove(p)

    # ---------- параметры страницы и стили ----------
    def _setup_page(self):
        s = self.doc.sections[0]
        s.orientation = WD_ORIENT.PORTRAIT
        s.page_width = Cm(21)
        s.page_height = Cm(29.7)
        s.top_margin = Cm(2)
        s.bottom_margin = Cm(2)
        s.left_margin = Cm(3)
        s.right_margin = Cm(1.5)
        s.gutter = Cm(0)

    def _setup_styles(self):
        # значения по умолчанию документа (docDefaults) — тоже TNR 14
        styles_el = self.doc.styles.element
        rpr_default = styles_el.find(qn("w:docDefaults")).find(qn("w:rPrDefault"))
        rpr = rpr_default.find(qn("w:rPr"))
        rfonts = rpr.find(qn("w:rFonts"))
        for attr in list(rfonts.attrib):
            del rfonts.attrib[attr]
        for attr in ("w:ascii", "w:hAnsi", "w:cs", "w:eastAsia"):
            rfonts.set(qn(attr), FONT)
        ppr_default = styles_el.find(qn("w:docDefaults")).find(qn("w:pPrDefault"))
        if ppr_default is not None:
            ppr = ppr_default.find(qn("w:pPr"))
            sp = ppr.find(qn("w:spacing")) if ppr is not None else None
            if sp is not None:
                sp.set(qn("w:after"), "0")
                sp.set(qn("w:line"), "360")
                sp.set(qn("w:lineRule"), "auto")

        normal = self.doc.styles["Normal"]
        _set_style_font(normal, BODY_PT)
        pf = normal.paragraph_format
        pf.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        pf.left_indent = Cm(0)
        pf.right_indent = Cm(0)
        pf.first_line_indent = FIRST_LINE
        pf.space_before = Pt(0)
        pf.space_after = Pt(0)
        pf.line_spacing = 1.5

    # ---------- низкоуровневые помощники ----------
    def _para(self, text="", *, size=BODY_PT, bold=False, align=None,
              first_line=None, left=None, keep_next=False):
        p = self.doc.add_paragraph()
        pf = p.paragraph_format
        if align is not None:
            pf.alignment = align
        if first_line is not None:
            pf.first_line_indent = first_line
        if left is not None:
            pf.left_indent = left
        if keep_next:
            pf.keep_with_next = True
        if text:
            _set_run_font(p.add_run(text), size, bold)
        else:
            # знак абзаца пустой строки тоже нужного размера
            rpr = p._p.get_or_add_pPr()
            mark = OxmlElement("w:rPr")
            sz = OxmlElement("w:sz")
            sz.set(qn("w:val"), str(size * 2))
            mark.append(sz)
            rpr.append(mark)
        return p

    def _page_break(self):
        p = self.doc.add_paragraph()
        p.paragraph_format.first_line_indent = Cm(0)
        p.add_run().add_break(WD_BREAK.PAGE)

    # ---------- титульный лист (копия образца из практикума) ----------
    def title_page(self, *, topic, variant, group="", student="",
                   teacher="доц. Смирнова Т.Н.", city_year="Чебоксары, 2026"):
        C, R = WD_ALIGN_PARAGRAPH.CENTER, WD_ALIGN_PARAGRAPH.RIGHT
        L = WD_ALIGN_PARAGRAPH.LEFT
        z = Cm(0)
        head = [
            "МИНИСТЕРСТВО НАУКИ И ВЫСШЕГО ОБРАЗОВАНИЯ РФ",
            "Федеральное государственное бюджетное образовательное",
            "учреждение высшего образования",
            "«Чувашский государственный университет имени И.Н. Ульянова»",
            "Факультет информатики и вычислительной техники",
            "Кафедра математического и аппаратного обеспечения информационных систем",
        ]
        for line in head:
            self._para(line, size=TITLE_PT, bold=True, align=C, first_line=z)
        for _ in range(2):
            self._para(size=TITLE_PT, align=C, first_line=z)
        self._para("Отчет по дисциплине «Языки объектно-ориентированного "
                   "программирования»", size=TITLE_PT, align=C, first_line=z)
        self._para(f"На тему «{topic}»", size=TITLE_PT, align=C, first_line=z)
        self._para(f"Вариант № {variant}", size=TITLE_PT, align=C, first_line=z)
        self._para(size=TITLE_PT, align=C, first_line=z)
        for _ in range(2):
            self._para(size=TITLE_PT, align=R, first_line=z)
        block_left = Cm(10.25)  # w:ind w:left="5812" в образце
        blank = "_" * 14
        self._para("Выполнил:", size=TITLE_PT, align=L, first_line=z, left=block_left)
        self._para(f"студент группы {group or blank}", size=TITLE_PT, align=L,
                   first_line=z, left=block_left)
        self._para(student or "_" * 20, size=TITLE_PT, align=L, first_line=z,
                   left=block_left)
        self._para(f"Принял: {teacher}", size=TITLE_PT, align=L, first_line=z,
                   left=block_left)
        for _ in range(4):
            self._para(size=TITLE_PT, align=R, first_line=z)
        for _ in range(3):
            self._para(size=TITLE_PT, align=L, first_line=z)
        self._para(size=TITLE_PT, align=C, first_line=z)
        self._para(city_year, size=TITLE_PT, align=C, first_line=z)
        self._page_break()

    # ---------- основной текст ----------
    def heading(self, text):
        """Заголовок раздела: те же параметры абзаца, полужирный, не отрывать
        от следующего абзаца."""
        return self._para(text, bold=True, keep_next=True)

    def text(self, text, keep_next=False):
        return self._para(text, keep_next=keep_next)

    def next_figure(self):
        return self.figure_no + 1

    def figure(self, caption, image_path=None, height_cm=None, width_cm=None):
        """Рисунок по центру без абзацного отступа и подпись под ним:
        «Рисунок N – Название». Без image_path оставляет пустое место
        под картинку (вставить вручную)."""
        self.figure_no += 1
        p = self._para(align=WD_ALIGN_PARAGRAPH.CENTER, first_line=Cm(0),
                       keep_next=True)
        if image_path:
            kw = {}
            if height_cm:
                kw["height"] = Cm(height_cm)
            if width_cm:
                kw["width"] = Cm(width_cm)
            p.add_run().add_picture(str(image_path), **kw)
        self._para(f"Рисунок {self.figure_no} – {caption}",
                   align=WD_ALIGN_PARAGRAPH.CENTER, first_line=Cm(0))
        return self.figure_no

    def table(self, caption, header, rows, widths_cm):
        """Таблица: над ней слева без отступа «Таблица N – Название»."""
        assert abs(sum(widths_cm) - TEXT_WIDTH_CM) < 0.01, sum(widths_cm)
        self.table_no += 1
        self._para(f"Таблица {self.table_no} – {caption}",
                   align=WD_ALIGN_PARAGRAPH.LEFT, first_line=Cm(0), keep_next=True)
        t = self.doc.add_table(rows=1 + len(rows), cols=len(header))
        t.style = self.doc.styles["Table Grid"]
        t.alignment = WD_TABLE_ALIGNMENT.CENTER
        t.autofit = False
        for r_i, values in enumerate([header] + rows):
            row = t.rows[r_i]
            for c_i, value in enumerate(values):
                cell = row.cells[c_i]
                cell.width = Cm(widths_cm[c_i])
                p = cell.paragraphs[0]
                pf = p.paragraph_format
                pf.first_line_indent = Cm(0)
                pf.line_spacing = 1.0
                pf.alignment = (WD_ALIGN_PARAGRAPH.CENTER if r_i == 0
                                else WD_ALIGN_PARAGRAPH.LEFT)
                if r_i == 0:
                    pf.keep_with_next = True
                _set_run_font(p.add_run(value), BODY_PT)
        # ширины колонок в сетке таблицы
        grid = t._tbl.tblGrid
        for gc, w in zip(grid.findall(qn("w:gridCol")), widths_cm):
            gc.set(qn("w:w"), str(int(Cm(w).twips)))
        # шапку повторять при переносе таблицы на следующую страницу
        trpr = t.rows[0]._tr.get_or_add_trPr()
        hdr = OxmlElement("w:tblHeader")
        trpr.append(hdr)
        return self.table_no

    def code(self, source):
        """Код программы: Times New Roman 14 пт, 1,5 строки, выравнивание по
        левому краю, без отступа первой строки; вложенность (по 4 пробела)
        передаётся отступом слева."""
        for line in source.rstrip("\n").split("\n"):
            line = line.rstrip().replace("\t", "    ")
            stripped = line.lstrip(" ")
            level = (len(line) - len(stripped)) // 4
            p = self._para(stripped, align=WD_ALIGN_PARAGRAPH.LEFT,
                           first_line=Cm(0), left=Cm(0.75 * level))
            p.paragraph_format.widow_control = True

    def save(self, path):
        # шаблон python-docx пишет <w:zoom> без обязательного w:percent
        zoom = self.doc.settings.element.find(qn("w:zoom"))
        if zoom is not None and zoom.get(qn("w:percent")) is None:
            zoom.set(qn("w:percent"), "100")
        self.doc.save(str(path))
