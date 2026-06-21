from pathlib import Path
import tkinter as tk
from tkinter import filedialog, messagebox, ttk

from podaci.file_operations import compress_file, decompress_file


class CompressionGui(tk.Tk):
    def __init__(self) -> None:
        super().__init__()
        self.title("Python implementacija")
        self.resizable(False, False)

        self.input_path = tk.StringVar()
        self.algorithm = tk.StringVar(value="lz78")
        self.operation = tk.StringVar(value="compress")

        self._build_ui()

    def _build_ui(self) -> None:
        main = ttk.Frame(self, padding=16)
        main.grid(row=0, column=0, sticky="nsew")

        ttk.Label(main, text="Fajl").grid(row=0, column=0, sticky="w")
        path_entry = ttk.Entry(main, textvariable=self.input_path, width=54)
        path_entry.grid(row=1, column=0, padx=(0, 8), pady=(4, 12), sticky="ew")
        ttk.Button(main, text="Izaberi", command=self._choose_input).grid(
            row=1, column=1, pady=(4, 12)
        )

        options = ttk.Frame(main)
        options.grid(row=2, column=0, columnspan=2, sticky="w", pady=(0, 12))

        ttk.Label(options, text="Algoritam").grid(row=0, column=0, sticky="w")
        ttk.Combobox(
            options,
            textvariable=self.algorithm,
            values=("lz78", "lzw"),
            state="readonly",
            width=8,
        ).grid(row=1, column=0, padx=(0, 24), pady=(4, 0), sticky="w")

        ttk.Label(options, text="Operacija").grid(row=0, column=1, sticky="w")
        ttk.Radiobutton(
            options,
            text="Enkripcija",
            variable=self.operation,
            value="compress",
        ).grid(row=1, column=1, padx=(0, 12), pady=(4, 0), sticky="w")
        ttk.Radiobutton(
            options,
            text="Dekripcija",
            variable=self.operation,
            value="decompress",
        ).grid(row=1, column=2, pady=(4, 0), sticky="w")

        ttk.Button(main, text="Pokreni", command=self._run).grid(
            row=3, column=0, columnspan=2, sticky="ew"
        )

    def _choose_input(self) -> None:
        file_name = filedialog.askopenfilename(title="Izaberi fajl")
        if file_name:
            self.input_path.set(file_name)

    def _run(self) -> None:
        input_file = Path(self.input_path.get())
        if not input_file.is_file():
            messagebox.showerror("Greska", "Izaberi postojeci fajl.")
            return

        algorithm = self.algorithm.get()
        operation = self.operation.get()
        default_name = self._default_output_name(input_file, algorithm, operation)
        output_file = filedialog.asksaveasfilename(
            title="Sacuvaj rezultat",
            initialdir=str(input_file.parent),
            initialfile=default_name,
        )
        if not output_file:
            return

        try:
            output_path = Path(output_file)
            if operation == "compress":
                compress_file(algorithm, input_file, output_path)
                message = f"Enkripcija je zavrsena:\n{output_path}"
            else:
                decompress_file(algorithm, input_file, output_path)
                message = f"Dekripcija je zavrsena:\n{output_path}"
            messagebox.showinfo("Gotovo", message)
        except Exception as exc:
            messagebox.showerror("Greska", str(exc))

    @staticmethod
    def _default_output_name(input_file: Path, algorithm: str, operation: str) -> str:
        if operation == "compress":
            return f"{input_file.stem}_{algorithm}_python.rkc"
        if input_file.suffix:
            return f"{input_file.stem}_dekompresovan{input_file.suffix}"
        return f"{input_file.name}_dekompresovan"


if __name__ == "__main__":
    CompressionGui().mainloop()
