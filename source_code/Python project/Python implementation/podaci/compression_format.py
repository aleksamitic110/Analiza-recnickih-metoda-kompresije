import struct


MAGIC = b"RKC26"
HEADER_STRUCT = struct.Struct(">5sBII")
ALG_LZ78 = 1
ALG_LZW = 2
LZW_CODE_BITS = 12


def pack_header(algorithm_id: int, original_size: int, token_count: int) -> bytes:
    return HEADER_STRUCT.pack(MAGIC, algorithm_id, original_size, token_count)


def unpack_header(data: bytes) -> tuple[int, int, int, bytes]:
    if len(data) < HEADER_STRUCT.size:
        raise ValueError("Fajl je prekratak i ne sadrzi ispravno zaglavlje.")

    magic, algorithm_id, original_size, token_count = HEADER_STRUCT.unpack(
        data[: HEADER_STRUCT.size]
    )
    if magic != MAGIC:
        raise ValueError("Nepoznat format fajla.")

    return algorithm_id, original_size, token_count, data[HEADER_STRUCT.size :]


def pack_fixed_width_codes(codes: list[int], width: int) -> bytes:
    bit_buffer = 0
    bit_count = 0
    output = bytearray()

    for code in codes:
        bit_buffer = (bit_buffer << width) | code
        bit_count += width
        while bit_count >= 8:
            bit_count -= 8
            output.append((bit_buffer >> bit_count) & 0xFF)

    if bit_count:
        output.append((bit_buffer << (8 - bit_count)) & 0xFF)
    return bytes(output)


def unpack_fixed_width_codes(payload: bytes, width: int, count: int) -> list[int]:
    codes: list[int] = []
    bit_buffer = 0
    bit_count = 0

    for byte in payload:
        bit_buffer = (bit_buffer << 8) | byte
        bit_count += 8
        while bit_count >= width and len(codes) < count:
            bit_count -= width
            codes.append((bit_buffer >> bit_count) & ((1 << width) - 1))

    if len(codes) != count:
        raise ValueError("LZW telo fajla nema ocekivan broj kodova.")
    return codes
