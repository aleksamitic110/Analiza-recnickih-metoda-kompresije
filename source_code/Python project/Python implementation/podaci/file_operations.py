import csv
import time
from pathlib import Path
from typing import Iterable

from algoritmi.lz78 import lz78_compress, lz78_decompress
from algoritmi.lzw import lzw_compress, lzw_decompress
from podaci.stats import CompressionStats, build_stats


def compress_file(algorithm: str, input_path: Path, output_path: Path) -> CompressionStats:
    data = input_path.read_bytes()
    start = time.perf_counter()

    if algorithm == "lz78":
        compressed, dictionary_size = lz78_compress(data)
    elif algorithm == "lzw":
        compressed, dictionary_size = lzw_compress(data)
    else:
        raise ValueError("Algoritam mora biti lz78 ili lzw.")

    elapsed_ms = (time.perf_counter() - start) * 1000
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_bytes(compressed)
    return build_stats(
        algorithm,
        input_path,
        len(data),
        len(compressed),
        dictionary_size,
        elapsed_ms,
    )


def decompress_file(algorithm: str, input_path: Path, output_path: Path) -> None:
    payload = input_path.read_bytes()
    if algorithm == "lz78":
        data = lz78_decompress(payload)
    elif algorithm == "lzw":
        data = lzw_decompress(payload)
    else:
        raise ValueError("Algoritam mora biti lz78 ili lzw.")

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_bytes(data)


def analyze_files(files: Iterable[Path], output_csv: Path) -> list[CompressionStats]:
    results: list[CompressionStats] = []
    output_csv.parent.mkdir(parents=True, exist_ok=True)

    for input_path in files:
        for algorithm in ("lz78", "lzw"):
            data = input_path.read_bytes()
            start = time.perf_counter()
            if algorithm == "lz78":
                compressed, dictionary_size = lz78_compress(data)
            else:
                compressed, dictionary_size = lzw_compress(data)
            elapsed_ms = (time.perf_counter() - start) * 1000
            results.append(
                build_stats(
                    algorithm,
                    input_path,
                    len(data),
                    len(compressed),
                    dictionary_size,
                    elapsed_ms,
                )
            )

    with output_csv.open("w", newline="", encoding="utf-8") as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=list(CompressionStats.__annotations__))
        writer.writeheader()
        for row in results:
            writer.writerow(row.__dict__)

    return results
