from dataclasses import dataclass
from pathlib import Path


@dataclass
class CompressionStats:
    algorithm: str
    input_file: str
    original_size: int
    compressed_size: int
    ratio: float
    saving_percent: float
    dictionary_size: int
    elapsed_ms: float


def build_stats(
    algorithm: str,
    input_path: Path,
    original_size: int,
    compressed_size: int,
    dictionary_size: int,
    elapsed_ms: float,
) -> CompressionStats:
    ratio = compressed_size / original_size if original_size else 0
    saving_percent = (1 - ratio) * 100 if original_size else 0
    return CompressionStats(
        algorithm=algorithm,
        input_file=str(input_path),
        original_size=original_size,
        compressed_size=compressed_size,
        ratio=round(ratio, 4),
        saving_percent=round(saving_percent, 2),
        dictionary_size=dictionary_size,
        elapsed_ms=round(elapsed_ms, 3),
    )
