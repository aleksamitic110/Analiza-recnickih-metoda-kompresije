import argparse
import json
import sys
from pathlib import Path

from podaci.file_operations import analyze_files, compress_file, decompress_file


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Recnicke metode kompresije: LZ78 i LZW."
    )
    subparsers = parser.add_subparsers(dest="command", required=True)

    compress = subparsers.add_parser("compress", help="Kompresuje fajl.")
    compress.add_argument("algorithm", choices=("lz78", "lzw"))
    compress.add_argument("-i", "--input", required=True, type=Path)
    compress.add_argument("-o", "--output", required=True, type=Path)

    decompress = subparsers.add_parser("decompress", help="Dekomprimuje fajl.")
    decompress.add_argument("algorithm", choices=("lz78", "lzw"))
    decompress.add_argument("-i", "--input", required=True, type=Path)
    decompress.add_argument("-o", "--output", required=True, type=Path)

    analyze = subparsers.add_parser("analyze", help="Meri odnos kompresije.")
    analyze.add_argument("files", nargs="+", type=Path)
    analyze.add_argument(
        "-o",
        "--output",
        default=Path("rezultati/analiza.csv"),
        type=Path,
        help="CSV fajl za rezultate.",
    )

    return parser


def main() -> None:
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    parser = build_parser()
    args = parser.parse_args()

    if args.command == "compress":
        stats = compress_file(args.algorithm, args.input, args.output)
        print(json.dumps(stats.__dict__, ensure_ascii=False, indent=2))
    elif args.command == "decompress":
        decompress_file(args.algorithm, args.input, args.output)
        print(f"Dekomprimovano: {args.output}")
    elif args.command == "analyze":
        results = analyze_files(args.files, args.output)
        print(f"Rezultati su upisani u: {args.output}")
        for row in results:
            print(
                f"{row.algorithm:4s} | {Path(row.input_file).name:20s} | "
                f"{row.original_size:7d} -> {row.compressed_size:7d} B | "
                f"odnos {row.ratio:.4f} | usteda {row.saving_percent:6.2f}%"
            )
