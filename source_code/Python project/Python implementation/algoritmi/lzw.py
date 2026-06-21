from podaci.compression_format import (
    ALG_LZW,
    LZW_CODE_BITS,
    pack_fixed_width_codes,
    pack_header,
    unpack_fixed_width_codes,
    unpack_header,
)


def lzw_compress(data: bytes, max_bits: int = LZW_CODE_BITS) -> tuple[bytes, int]:
    if max_bits != LZW_CODE_BITS:
        raise ValueError("Projektni format koristi fiksne 12-bitne LZW kodove.")
    if not data:
        return pack_header(ALG_LZW, 0, 0), 256

    max_dictionary_size = 1 << max_bits
    dictionary = {bytes([i]): i for i in range(256)}
    next_code = 256
    current = bytes([data[0]])
    codes: list[int] = []

    for byte in data[1:]:
        candidate = current + bytes([byte])
        if candidate in dictionary:
            current = candidate
            continue

        codes.append(dictionary[current])
        if next_code < max_dictionary_size:
            dictionary[candidate] = next_code
            next_code += 1
        current = bytes([byte])

    codes.append(dictionary[current])

    body = bytearray()
    body.extend(pack_header(ALG_LZW, len(data), len(codes)))
    body.extend(pack_fixed_width_codes(codes, LZW_CODE_BITS))
    return bytes(body), len(dictionary)


def lzw_decompress(payload: bytes) -> bytes:
    algorithm_id, original_size, token_count, body = unpack_header(payload)
    if algorithm_id != ALG_LZW:
        raise ValueError("Fajl nije kompresovan LZW algoritmom.")
    if token_count == 0:
        return b""

    codes = unpack_fixed_width_codes(body, LZW_CODE_BITS, token_count)

    dictionary = {i: bytes([i]) for i in range(256)}
    next_code = 256
    previous = dictionary[codes[0]]
    result = bytearray(previous)

    for code in codes[1:]:
        if code in dictionary:
            entry = dictionary[code]
        elif code == next_code:
            entry = previous + previous[:1]
        else:
            raise ValueError(f"Neispravan LZW kod: {code}")

        result.extend(entry)
        dictionary[next_code] = previous + entry[:1]
        next_code += 1
        previous = entry

    output = bytes(result)
    if len(output) != original_size:
        raise ValueError("Dekomprimovana duzina se ne poklapa sa zaglavljem.")
    return output
